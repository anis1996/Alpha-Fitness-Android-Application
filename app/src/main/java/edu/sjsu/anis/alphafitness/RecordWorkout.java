package edu.sjsu.anis.alphafitness;

import android.content.res.Configuration;
import android.support.v4.app.Fragment;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class RecordWorkout extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_workout);

        FragmentManager fm = getSupportFragmentManager();


        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {


        } else {

            Fragment fragment = fm.findFragmentById(R.id.fragmentcontainer);
                fragment = new WorkoutFragment();
                fm.beginTransaction()
                        .replace(R.id.fragmentcontainer, fragment)
                        .commit();

        }

    }
}
