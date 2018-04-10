package edu.sjsu.anis.alphafitness;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
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
import edu.sjsu.anis.alphafitness.DataBase.RecordContract.Contracts;

public class ProfileActivity extends AppCompatActivity {


    private TextView userNameTV, genderTV, weightTV;
    private TextView avgDistanceTV, avgTimeTV, avgWorkoutsTV, avgCaloriesBurnedTV;
    private TextView allTimeDistanceTV, allTimeTimeTV, allTimeWorkoutsTV, allTimeCaloriesBurnedTV;
    private ImageView profileIcon;

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

    }

    public boolean onOptionsItemSelected(MenuItem item){
//        Intent myIntent = new Intent(getApplicationContext()
//                , RecordWorkout.class);
//        startActivityForResult(myIntent, 0);
//        return true;
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


//        Toast.makeText(this, "we are in ",
//                Toast.LENGTH_LONG).show();

    }




}
