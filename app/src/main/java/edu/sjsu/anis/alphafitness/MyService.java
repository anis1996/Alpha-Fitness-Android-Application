package edu.sjsu.anis.alphafitness;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;


public class MyService extends Service {
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


    public MyService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        points = new ArrayList<LatLng>();


       mBinder = new IMyAidlInterface.Stub() {
           @Override
           public void startWorkout(long s) throws RemoteException {
               startTime = SystemClock.uptimeMillis();
               WorkoutFragment.handler.postDelayed(timerRunnable,20);
               points = new ArrayList<LatLng>();
               WorkoutFragment.polyLineHandler.postDelayed(locationRunnable,20);



               return ;
           }

           @Override
           public void stopWorkout() throws RemoteException{
               WorkoutFragment.handler.removeCallbacks(timerRunnable);
               Message msg1 = new Message();
               msg1.obj = "00:00:00";
               mSecondTime = 0L;
               seconds = 0;
               minutes = 0;
               mSeconds = 0;
               points.clear();
               WorkoutFragment.polyLineHandler.removeCallbacks(locationRunnable);
               WorkoutFragment.handler.sendMessage(msg1);

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
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
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
            WorkoutFragment.handler.postDelayed(this, 10);
        }
    };
}
