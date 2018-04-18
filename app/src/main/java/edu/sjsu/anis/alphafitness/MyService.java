package edu.sjsu.anis.alphafitness;

import android.Manifest;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import edu.sjsu.anis.alphafitness.DataBase.RecordContract;


public class MyService extends Service implements SensorEventListener{
    static final double METER_PER_STEP_MEN = 0.762;
    static final double METER_PER_STEP_WOMEN = 0.67;
    static final double MILE_PER_METER = 0.000621371;


    //Timer
    int mSeconds, seconds, minutes;
    long mSecondTime, startTime, updateTime;
    long TimeBuff = 0L;



    String result;


    LocationManager locationManager;
    Location location;
    private ArrayList<LatLng> points = null;
    private LocationListener locationListener;

    IMyAidlInterface.Stub mBinder;


    //sensors
    private SensorManager sManager;
    private Sensor stepSensor;

    //distance and time
    int steps = 0 ;
    float initialDistance = 0;
    long initialTime = 0;


    //database
    long totalTimes;
    int totalWorkoutNumber;
    float totalDistance;


    public static Boolean stat = true;

    public MyService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        points = new ArrayList<LatLng>();

        sManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepSensor = sManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

        Cursor cursor = getContentResolver().query(MyContentProvider.CONTENT_URI, null, "_id = ?", new String[]{"1"}, RecordContract.Contracts._ID);


        if (cursor.moveToFirst()) {

            totalTimes = cursor.getInt(cursor.getColumnIndex(RecordContract.Contracts.ALL_TIME_KEY_TIME));
            totalWorkoutNumber = cursor.getInt(cursor.getColumnIndex(RecordContract.Contracts.ALL_TIME_KEY_NUM_OF_WORKOUTS));
            totalDistance = cursor.getFloat(cursor.getColumnIndex(RecordContract.Contracts.ALL_TIME_KEY_DISTANCE));


        }


       mBinder = new IMyAidlInterface.Stub() {
           @Override
           public void startWorkout() throws RemoteException {
               startTime = SystemClock.uptimeMillis();
               WorkoutFragment.handler.postDelayed(timerRunnable,20);
               points = new ArrayList<LatLng>();
               stat = true;
               WorkoutFragment.polyLineHandler.postDelayed(locationRunnable,20);
               sManager.registerListener(MyService.this, stepSensor, SensorManager.SENSOR_DELAY_FASTEST);
               steps = 0;
               initialTime = 0;
               initialDistance = 0;


               return ;
           }

           @Override
           public void stopWorkout() throws RemoteException{
               WorkoutFragment.handler.removeCallbacks(timerRunnable);
               stat = false;
               Message msg1 = new Message();
               msg1.obj = "00:00:00";
               mSecondTime = 0L;
               seconds = 0;
               minutes = 0;
               mSeconds = 0;
               points.clear();
               sManager.unregisterListener(MyService.this, stepSensor);
               WorkoutFragment.polyLineHandler.removeCallbacks(locationRunnable);
               WorkoutFragment.handler.sendMessage(msg1);
               steps = 0;
               ContentValues contentValues = new ContentValues();
               totalWorkoutNumber += 1;
               contentValues.put(RecordContract.Contracts.ALL_TIME_KEY_NUM_OF_WORKOUTS, totalWorkoutNumber);
               getContentResolver().update(MyContentProvider.CONTENT_URI, contentValues, "_id = ?", new String[] {"1"});
           }

       };
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return mBinder;
    }

    public Runnable locationRunnable = new Runnable(){

        @Override
        public void run() {

            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            Criteria criteria = new Criteria();
            String locationProvider = locationManager.getBestProvider(criteria, false);


            if (ActivityCompat.checkSelfPermission(MyService.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(MyService.this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                return;
            }

            location = locationManager.getLastKnownLocation(locationProvider);
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            points.add(latLng);
            Message locationMsg = new Message();
            locationMsg.obj = points;
            WorkoutFragment.polyLineHandler.sendMessage(locationMsg);
            WorkoutFragment.polyLineHandler.postDelayed(this, 10);
        }
    };


    public Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {

            if(stat) {
                mSecondTime = SystemClock.uptimeMillis() - startTime;
                updateTime = TimeBuff + mSecondTime;
                seconds = (int) (updateTime / 1000);
                minutes = seconds / 60;
                seconds = seconds % 60;
                mSeconds = (int) (updateTime % 100);
                result = String.format("" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds)
                        + ":" + String.format("%02d", mSeconds));
                Message msg = new Message();
                msg.obj = result;
                WorkoutFragment.handler.sendMessage(msg);

                Message stepMsg = new Message();
                float calDistance = steps * (float) METER_PER_STEP_MEN;
                calDistance *= MILE_PER_METER ;
                stepMsg.obj = calDistance;
                WorkoutFragment.distanceHandler.sendMessage(stepMsg);


                ContentValues contentValues = new ContentValues();
                float dischange = (calDistance - initialDistance);
                initialDistance = calDistance;
                totalDistance = totalDistance + dischange;
                long changeTime = (mSecondTime - initialTime) ;
                initialTime = mSecondTime;
                totalTimes += changeTime;
                contentValues.put(RecordContract.Contracts.ALL_TIME_KEY_TIME, totalTimes);
                contentValues.put(RecordContract.Contracts.ALL_TIME_KEY_DISTANCE, totalDistance );
                getContentResolver().update(MyContentProvider.CONTENT_URI, contentValues, "_id = ?", new String[] {"1"});

                WorkoutFragment.handler.postDelayed(this,20);
            }
        }
    };

    @Override
    public void onSensorChanged(SensorEvent event) {

        Sensor sensor = event.sensor;
        float[] values = event.values;
        int value = -1;

        if (values.length > 0) {
            value = (int) values[0];
        }


        if (sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            steps++;

        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }


}
