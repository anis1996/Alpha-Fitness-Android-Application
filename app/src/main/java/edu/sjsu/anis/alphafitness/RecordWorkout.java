package edu.sjsu.anis.alphafitness;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class RecordWorkout extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_workout);

        Configuration config = getResources().getConfiguration();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction =
                fragmentManager.beginTransaction();
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragmentcontainer);

        if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            fragment = new BlankFragment();
            fragmentTransaction.replace(R.id.fragmentcontainer,fragment);
        } else {
            fragment = new WorkoutFragment();
            fragmentTransaction.replace(R.id.fragmentcontainer, fragment);
        }

        fragmentTransaction.commit();
    }

    public void openProfile(View view) {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

}
