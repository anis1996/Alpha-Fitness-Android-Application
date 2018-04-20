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


                //TOTAL WORKOUT
               long totalTimes = cursor.getInt(cursor.getColumnIndex(RecordContract.Contracts.ALL_TIME_KEY_TIME));
                int totalWorkoutNumber = cursor.getInt(cursor.getColumnIndex(RecordContract.Contracts.ALL_TIME_KEY_NUM_OF_WORKOUTS));
               float totalDistance = cursor.getFloat(cursor.getColumnIndex(RecordContract.Contracts.ALL_TIME_KEY_DISTANCE));
               float totalCalories = cursor.getFloat(cursor.getColumnIndex(Contracts.ALL_TIME_KEY_CALORIES_BURNED));

               allTimeDistanceTV.setText(String.format("%.2f", totalDistance));
               allTimeWorkoutsTV.setText(String.valueOf(totalWorkoutNumber));
               allTimeCaloriesBurnedTV.setText(String.format("%.1f", totalCalories));

                int seconds = (int) (totalTimes / 1000);
                int minutes = seconds / 60;
                 seconds = seconds % 60;
                int mSeconds = (int) (totalTimes % 100);
                String result = String.format("" + String.format("%02d Min", minutes) + ":" + String.format("%02d Sec", seconds));
                allTimeTimeTV.setText(result);

                //WEEKLY WORKOUT
                long weeklyTimes = cursor.getInt(cursor.getColumnIndex(RecordContract.Contracts.KEY_TIME));
                int weeklyWorkoutNumber = cursor.getInt(cursor.getColumnIndex(RecordContract.Contracts.KEY_NUM_OF_WORKOUTS));
                float weeklyDistance = cursor.getFloat(cursor.getColumnIndex(RecordContract.Contracts.KEY_DISTANCE));
                float weeklyCalories = cursor.getFloat(cursor.getColumnIndex(Contracts.KEY_CALORIES_BURNED));


                avgDistanceTV.setText(String.format("%.2f",weeklyDistance));
                avgWorkoutsTV.setText(String.valueOf(weeklyWorkoutNumber));
                avgCaloriesBurnedTV.setText(String.format("%.1f", weeklyCalories));

                int Wseconds = (int) (weeklyTimes / 1000);
                int Wminutes = Wseconds / 60;
                Wseconds = Wseconds % 60;
                int WmSeconds = (int) (weeklyTimes % 100);
                String Wresult = String.format("" + String.format("%02d Min", Wminutes) + ":" + String.format("%02d Sec", Wseconds));
                avgTimeTV.setText(Wresult);

                workoutHandler.postDelayed(updateAvgWeeklyRunnable, 1000);
            }
        }
    };




}
