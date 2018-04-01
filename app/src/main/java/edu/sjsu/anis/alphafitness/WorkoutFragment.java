package edu.sjsu.anis.alphafitness;

import android.Manifest;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import edu.sjsu.anis.alphafitness.DataBase.RecordContract.Contracts;



/**
 */
public class WorkoutFragment extends Fragment {

    static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 10001;
    //Google Map
    private GoogleMap googleMap;
    private LocationManager locationManager;
    Location location;





    private SharedPreferences sharedPreferences;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_workout, container, false);
        defaultDatabase();

        MapView mMapView = (MapView) view.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
        {
           return view;
        }
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {

                googleMap = mMap;

//                CameraUpdate zoom= CameraUpdateFactory.zoomTo(15);
//                mMap.animateCamera(zoom);

                locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

                Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_FINE);
                criteria.setPowerRequirement(Criteria.POWER_LOW);
                String locationProvider = locationManager.getBestProvider(criteria, true);

                /*
                 * Request location permission, so that we can get the location of the
                 * device. The result of the permission request is handled by a callback,
                 * onRequestPermissionsResult.
                 */
                if (ActivityCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                    return ;

                }


                location = locationManager.getLastKnownLocation(locationProvider);
                LatLng here = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.addMarker(new MarkerOptions().position(here).title("my address"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(here));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(here,
                        19));

            }
        });

        return view;

    }

    public void defaultDatabase() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contracts.KEY_NAME, "Anis Dhapa");
        contentValues.put(Contracts.KEY_GENDER, "Male");
        contentValues.put(Contracts.KEY_WEIGHT, 150);
        contentValues.put(Contracts.KEY_DISTANCE, 0);
        contentValues.put(Contracts.KEY_CALORIES_BURNED, 0);
        contentValues.put(Contracts.KEY_NUM_OF_WORKOUTS, 0);
        contentValues.put(Contracts.KEY_TIME, "0");


        getActivity().getContentResolver().insert(MyContentProvider.CONTENT_URI, contentValues);
    }





}
