package com.example.compassmap;

import android.Manifest;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.compassmap.databinding.ActivityMapsBinding;

import java.util.Map;

//==================================================================================================
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, SensorEventListener {
    //----------------------------------------------------------------------------------------------
    private GoogleMap myMap;
    private ActivityMapsBinding binding;
    private SensorManager sensorManager;
    private LocationRequest locationRequest;
    private FusedLocationProviderClient fusedLocationClient;
    private float[] magneticField = new float[3];
    private boolean magneticFieldAvailable;
    private float[] gravity = new float[3];
    private boolean gravityAvailable;
    private float[] orientation = new float[3];
    private Location lastLocation;
    //----------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPermissions.launch(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION});

    }
    //----------------------------------------------------------------------------------------------
    public void goOnCreating(boolean havePermission){
        if(havePermission){
            //sets content view
            binding = ActivityMapsBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());
            //gets sensor manager
            sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
            //gets fusedlocationclient
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            //make location requests
            locationRequest = new LocationRequest();
            locationRequest.setInterval(getResources().getInteger(R.integer.time_between_location_updates_ms));
            locationRequest.setFastestInterval(getResources().getInteger(R.integer.shortest_time_between_location_updates_ms));
            locationRequest.setPriority(Priority.PRIORITY_HIGH_ACCURACY);
            try {
                fusedLocationClient.requestLocationUpdates(locationRequest,
                        myLocationCallback, Looper.myLooper());
            } catch (SecurityException e) {
                Toast.makeText(this,"Permission denied",Toast.LENGTH_SHORT).show();
            }
            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }else{
            Toast.makeText(this,"Need permissions",Toast.LENGTH_LONG).show();
            finish();
        }
    }
    //----------------------------------------------------------------------------------------------
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        //saves map
        myMap = googleMap;

        //sets zoom control and satellite view
        myMap.getUiSettings().setZoomControlsEnabled(true);
        myMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        //----Add a marker in UM and move the camera
        LatLng uM = new LatLng(25.721428,-80.279962);
        myMap.addMarker(new MarkerOptions().position(uM).title("UM"));
        myMap.moveCamera(CameraUpdateFactory.newLatLng(uM));
    }
    //----------------------------------------------------------------------------------------------
    @Override
    public void onSensorChanged(SensorEvent event) {

        float[] rotation = new float[9];
        float[] inclination = new float[9];
        float[] newOrientation = new float[3];
        boolean gravityChanged ,magneticFieldChanged;

        gravityChanged = magneticFieldChanged =  false;

        switch(event.sensor.getType()){
            case Sensor.TYPE_ACCELEROMETER:
                gravityChanged = arrayCopyChangeTest(event.values,gravity,
                        Float.parseFloat(getResources().getString(R.string.minimum_gravity_change)));
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                magneticFieldChanged = arrayCopyChangeTest(event.values,magneticField,
                        Float.parseFloat(getResources().getString(R.string.minimum_magnetic_change)));
                break;
            default:
                break;
        }
        if((gravityChanged || magneticFieldChanged) && SensorManager.getRotationMatrix(rotation,inclination,gravity,magneticField)){
            SensorManager.getOrientation(rotation,newOrientation);
            newOrientation[0] = (float)Math.toDegrees(newOrientation[0]);
            newOrientation[1] = (float)Math.toDegrees(newOrientation[1]);
            newOrientation[2] = (float)Math.toDegrees(newOrientation[2]);
            arrayCopyChangeTest(newOrientation,orientation,5);
            buildCamera();
        }
    }
    //----------------------------------------------------------------------------------------------
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
    //
    LocationCallback myLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {

            if (locationResult == null || locationResult.getLastLocation() == null) {
                Toast.makeText(MapsActivity.this, "No location", Toast.LENGTH_SHORT).show();
                return;
            }

            if(lastLocation == null) {
                lastLocation = locationResult.getLastLocation();
                myMap.moveCamera(CameraUpdateFactory.newLatLng(locationToLatLong(lastLocation)));
                //start sensors
                magneticFieldAvailable = startSensor(Sensor.TYPE_MAGNETIC_FIELD);
                gravityAvailable = startSensor(Sensor.TYPE_ACCELEROMETER);
                //build camera
                buildCamera();
            } else if (lastLocation.distanceTo(locationResult.getLastLocation()) > 10000) {
                lastLocation = locationResult.getLastLocation();
                myMap.moveCamera(CameraUpdateFactory.newLatLng(locationToLatLong(lastLocation)));
            }
        }
    };
    //----------------------------------------------------------------------------------------------
    private LatLng locationToLatLong(Location location){
        LatLng latLngOfLocation;
        latLngOfLocation = new LatLng(location.getLatitude(),location.getLongitude());
        return latLngOfLocation;
    }
    //----------------------------------------------------------------------------------------------
    private void buildCamera(){
        CameraPosition position = new CameraPosition.Builder()
                .target(locationToLatLong(lastLocation))
                .tilt(60)
                .zoom(15)
                .bearing(orientation[0])
                .build();
        myMap.animateCamera(CameraUpdateFactory.newCameraPosition(position));
    }
    //----------------------------------------------------------------------------------------------
    public boolean startSensor(int sensorType){

        if(sensorManager.getSensorList(sensorType).isEmpty()){
            return false;
        }else{
            sensorManager.registerListener(this,sensorManager.getDefaultSensor(sensorType), SensorManager.SENSOR_DELAY_GAME);
            //Toast.makeText(this,"Sensors On", Toast.LENGTH_SHORT).show();
            return true;
        }
    }
    //----------------------------------------------------------------------------------------------
    private boolean arrayCopyChangeTest(float[] from,float[] to,float amountForChange) {

        int copyIndex;
        boolean changed = false;

        for (copyIndex=0;copyIndex < to.length;copyIndex++) {
            if (Math.abs(from[copyIndex] - to[copyIndex]) > amountForChange) {
                changed = true;
            }
        }
        if (changed) {
            for (copyIndex = 0; copyIndex < to.length; copyIndex++) {
                to[copyIndex] = from[copyIndex];
            }
        }
        return(changed);
    }
    //----------------------------------------------------------------------------------------------
    private ActivityResultLauncher<String[]> getPermissions = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            new ActivityResultCallback<Map<String, Boolean>>() {
                @Override
                public void onActivityResult(Map<String, Boolean> results) {

                    for (String key:results.keySet()) {
                        if (!results.get(key)) {
                            goOnCreating(false);
                        }
                    }
                    goOnCreating(true);
                }
            });
    //----------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------
    @Override
    public void onDestroy() {

        super.onDestroy();
        sensorManager.unregisterListener(this);
        fusedLocationClient.removeLocationUpdates(myLocationCallback);

    }
}
//==================================================================================================