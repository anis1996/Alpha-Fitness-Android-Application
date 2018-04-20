package edu.sjsu.anis.alphafitness;

import android.Manifest;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

import edu.sjsu.anis.alphafitness.DataBase.RecordContract.Contracts;

import static android.content.Context.BIND_AUTO_CREATE;
import static android.content.Context.MODE_PRIVATE;
import static java.lang.Math.abs;


/**
 */
public class WorkoutFragment extends Fragment {

    static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 10001;


    //UI Elements
    TextView timer;
    Button recordButton;
    boolean record ;
    TextView distanceUI;



    //Google Map
    private GoogleMap googleMap;
    private LocationManager locationManager;
    Location location;
    private FusedLocationProviderClient fusedLocationProviderClient;
    ArrayList<LatLng> points = new ArrayList<>();





    //handlers
    static Handler handler;
    static Handler polyLineHandler;
    static Handler distanceHandler;


    private SharedPreferences sharedPreferences;

    IMyAidlInterface remoteService;
    RemoteConnection remoteConnection = null;


    class RemoteConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            remoteService = IMyAidlInterface.Stub.asInterface((IBinder) service);
            Toast.makeText(getActivity(),
                    "Remote Service connected.", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            remoteService = null;
//            Toast.makeText(MainActivity.this,
//                    "Remote Service disconnected.", Toast.LENGTH_LONG).show();
        }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("CCCCCCCCCCCCCCCCCCCCCC", "CREATE CCCCCCC");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_workout, container, false);
        defaultDatabase(); // inserting default data if table is empty

        record = true;

        timer = (TextView) view.findViewById(R.id.duration);
        recordButton = (Button) view.findViewById(R.id.recordButton);
        distanceUI = (TextView) view.findViewById(R.id.distance);

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

        SharedPreferences prefs = getActivity().getSharedPreferences("mySharedPref", MODE_PRIVATE);
        Boolean recordWorkout = prefs.getBoolean("recordWorkout", false);

        if(!recordWorkout)
        {
            Log.d("CCCCCCCCCCCCCCCCCCCCCC", "SharedPre");
            recordButton.setText(" STOP WORKOUT ");
            record = false;
        }


        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {

                googleMap = mMap;

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

                googleMap.setMyLocationEnabled(true);

                fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
                Task locationResult = fusedLocationProviderClient.getLastLocation();

                locationResult.addOnCompleteListener(getActivity(), new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            location = (Location) task.getResult();
                            if (location != null) {
                                LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());

                                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(location.getLatitude(),
                                                location.getLongitude()), 19));


                            } else {
                                Toast.makeText(getActivity(), "Cannot get current location. Please turn on GPS or allow permission request.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            LatLng mDefaultLocation = new LatLng(30, -121); //Default is San Jose
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation,19));
                            googleMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        });



        handler = new Handler()
        {
            @Override
            public void handleMessage(Message msg) {
                timer.setText((String) msg.obj);
            }
        };

        polyLineHandler = new Handler()
        {
            @Override
            public void handleMessage(Message msg) {

///                points = (ArrayList<LatLng> ) msg.obj;
                PolylineOptions options = new PolylineOptions().width(10).color(Color.RED).geodesic(true);
                for (int i = 0; i < points.size() ; i++) {
                    LatLng point = points.get(i);
                    options.add(point);
                }
                if(googleMap.getMyLocation() != null) {
                    points.add(new LatLng(googleMap.getMyLocation().getLatitude(), googleMap.getMyLocation().getLongitude()));
                    Polyline line = googleMap.addPolyline(options);
                    line.setPoints(points);
                }
            }
        };

        distanceHandler = new Handler()
        {
            @Override
            public void handleMessage(Message msg) {

                float calDistance = (float) msg.obj;
//                Toast.makeText(getActivity(), "Distance: " + distance, Toast.LENGTH_SHORT).show();
                distanceUI.setText(String.format("%.3f", calDistance));

            }
        };


        remoteConnection = new RemoteConnection();
        Intent intent = new Intent();
        intent.setClassName("edu.sjsu.anis.alphafitness",
               edu.sjsu.anis.alphafitness.MyService.class.getName());

        if (!getActivity().bindService(intent, remoteConnection, BIND_AUTO_CREATE)) {
            Toast.makeText(getActivity(),
                    "Fail to bind the remote service.", Toast.LENGTH_LONG).show();
        }


        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = getActivity().getSharedPreferences("mySharedPref", MODE_PRIVATE).edit();
                if(record )
                {

                    recordButton.setText(" STOP WORKOUT ");
                   // handler.postDelayed(timerRunnable, 10);
                    String result = "";
                    try {

                        remoteService.startWorkout();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                    editor.putBoolean("recordWorkout", false);
                    editor.apply();
                    record = false;
                }else
                {

                    record = true ;
                    try {
                        remoteService.stopWorkout();

                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    recordButton.setText(" START WORKOUT ");
                    editor.putBoolean("recordWorkout", true);
                    editor.apply();
                    distanceUI.setText("0.000");
                    timer.setText("00:00:00");
                    googleMap.clear();
                    points.clear();
//                    handler.removeCallbacks(timerRunnable);
                }
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
        contentValues.put(Contracts.ALL_TIME_KEY_DISTANCE, 0);
        contentValues.put(Contracts.ALL_TIME_KEY_CALORIES_BURNED, 0);
        contentValues.put(Contracts.ALL_TIME_KEY_NUM_OF_WORKOUTS, 0);
        contentValues.put(Contracts.ALL_TIME_KEY_TIME, "0");
        getActivity().getContentResolver().insert(MyContentProvider.CONTENT_URI, contentValues);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("CCCCCCCCCCCCCCCCCCCCCC", "RESUME CCCCCCC");

    }

}
