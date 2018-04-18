package edu.sjsu.anis.alphafitness;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import edu.sjsu.anis.alphafitness.DataBase.MyDataBase;
import edu.sjsu.anis.alphafitness.DataBase.RecordContract;
import edu.sjsu.anis.alphafitness.DataBase.RecordContract.Contracts;

public class ProfileActivity extends AppCompatActivity {


    private TextView userNameTV, genderTV, weightTV;
    private TextView avgDistanceTV, avgTimeTV, avgWorkoutsTV, avgCaloriesBurnedTV;
    private TextView allTimeDistanceTV, allTimeTimeTV, allTimeWorkoutsTV, allTimeCaloriesBurnedTV;
    private ImageView profileIcon;



    private Handler workoutHandler ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


        userNameTV = (TextView) findViewById(R.id.username);
        genderTV = (TextView) findViewById(R.id.gender);
        weightTV = (TextView) findViewById(R.id.weight);
        avgDistanceTV = (TextView) findViewById(R.id.avg_distance);
        avgTimeTV = (TextView) findViewById(R.id.avg_time);
        avgWorkoutsTV = (TextView) findViewById(R.id.avg_workouts);
        avgCaloriesBurnedTV = (TextView) findViewById(R.id.avg_calories);
        allTimeDistanceTV = (TextView) findViewById(R.id.total_distance);
        allTimeTimeTV = (TextView) findViewById(R.id.total_time);
        allTimeWorkoutsTV = (TextView) findViewById(R.id.total_workouts);
        allTimeCaloriesBurnedTV = (TextView) findViewById(R.id.total_calories);
        profileIcon = (ImageView) findViewById(R.id.imageView);





        Cursor cursor = getContentResolver().query(MyContentProvider.CONTENT_URI, null, "_id = ?", new String[]{"1"}, Contracts._ID);


        if (cursor.moveToFirst()) {


                String name = cursor.getString(cursor.getColumnIndex(Contracts.KEY_NAME));
                String gender = cursor.getString(cursor.getColumnIndex(Contracts.KEY_GENDER));
                double weight = cursor.getDouble(cursor.getColumnIndex(Contracts.KEY_WEIGHT));


                userNameTV.setText(name);
                genderTV.setText(gender);
                weightTV.setText(String.valueOf(weight));


        }

        workoutHandler = new Handler();
        workoutHandler.postDelayed(updateAvgWeeklyRunnable, 1000);

    }

    public boolean onOptionsItemSelected(MenuItem item){

        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);

    }

    public void saveProfile(View v)
    {

        ContentValues contentValues = new ContentValues();
        contentValues.put(Contracts.KEY_NAME,userNameTV.getText().toString());
        contentValues.put(Contracts.KEY_GENDER,genderTV.getText().toString());
        contentValues.put(Contracts.KEY_WEIGHT,Double.parseDouble(weightTV.getText().toString()) );


        getContentResolver().update(MyContentProvider.CONTENT_URI, contentValues, "_id = ?", new String[] {"1"});


    }

    private Runnable updateAvgWeeklyRunnable = new Runnable() {
        @Override
        public void run() {
            Cursor cursor = getContentResolver().query(MyContentProvider.CONTENT_URI, null, "_id = ?", new String[]{"1"}, Contracts._ID);


            if (cursor.moveToFirst()) {


               long totalTimes = cursor.getInt(cursor.getColumnIndex(RecordContract.Contracts.ALL_TIME_KEY_TIME));
                int totalWorkoutNumber = cursor.getInt(cursor.getColumnIndex(RecordContract.Contracts.ALL_TIME_KEY_NUM_OF_WORKOUTS));
               float totalDistance = cursor.getFloat(cursor.getColumnIndex(RecordContract.Contracts.ALL_TIME_KEY_DISTANCE));

               allTimeDistanceTV.setText(String.valueOf(totalDistance));
               allTimeWorkoutsTV.setText(String.valueOf(totalWorkoutNumber));


                int seconds = (int) (totalTimes / 1000);
                int minutes = seconds / 60;
                 seconds = seconds % 60;
                int mSeconds = (int) (totalTimes % 100);
                String result = String.format("" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds)
                        + ":" + String.format("%02d", mSeconds));
                allTimeTimeTV.setText(result);
                workoutHandler.postDelayed(updateAvgWeeklyRunnable, 1000);
            }
        }
    };




}
