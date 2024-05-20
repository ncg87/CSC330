package com.example.facinghome;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import android.location.Location;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Looper;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Map;

//==================================================================================================
public class MainActivity extends AppCompatActivity implements SensorEventListener {
    //----------------------------------------------------------------------------------------------
    private float[] magneticField = new float[3];
    private boolean magneticFieldAvailable;
    private float[] gravity = new float[3];
    private boolean gravityAvailable;
    private float[] orientation = new float[3];
    private boolean alreadyBeeping;
    private SensorManager sensorManager;
    private LocationRequest locationRequest;
    private float birthPlaceBearing;
    private FusedLocationProviderClient fusedLocationClient;
    private ToneGenerator beeper;
    private ProgressBar myProgressBar;
    //----------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        getPermissions.launch(new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION});

    }
    //----------------------------------------------------------------------------------------------
    public void goOnCreating(boolean havePermission){

        if(havePermission) {

            setContentView(R.layout.activity_main);
            //gets progress bar and set to empty
            myProgressBar = findViewById(R.id.myProgressBar);
            myProgressBar.setProgress(0);
            //gets sensor manager
            sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
            //gets beeper
            beeper = new ToneGenerator(AudioManager.STREAM_MUSIC,ToneGenerator.MAX_VOLUME);
            alreadyBeeping = false;
            //gets location
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            locationRequest = new LocationRequest();
            locationRequest.setPriority(Priority.PRIORITY_HIGH_ACCURACY);
            try {
                fusedLocationClient.requestLocationUpdates(locationRequest,
                        myLocationCallback, Looper.myLooper());
            } catch (SecurityException e) {
                Toast.makeText(this,"Permission denied",Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this,"Need permission",Toast.LENGTH_LONG).show();
            finish();
        }
    }
    //----------------------------------------------------------------------------------------------
    LocationCallback myLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {

            //stops locator
            fusedLocationClient.removeLocationUpdates(myLocationCallback);
            //gets current location
            Location currentLocation = locationResult.getLastLocation();

            //set location of birthplace
            Location locationBirthplace = new Location("");
            locationBirthplace.setLongitude(-106.6504);
            locationBirthplace.setLatitude(35.0844);
            //get bearing to birth place
            birthPlaceBearing = currentLocation.bearingTo(locationBirthplace);

            if (locationResult == null || locationResult.getLastLocation() == null) {
                Toast.makeText(MainActivity.this,"No location",Toast.LENGTH_SHORT).show();
                return;
            }

            //gets accelerometer and magnetic field for orientation
            magneticFieldAvailable = startSensor(Sensor.TYPE_MAGNETIC_FIELD);
            gravityAvailable = startSensor(Sensor.TYPE_ACCELEROMETER);
        }
    };
    //----------------------------------------------------------------------------------------------
    public boolean startSensor(int sensorType){

        if(sensorManager.getSensorList(sensorType).isEmpty()){
            return false;
        }else{
            sensorManager.registerListener(this,sensorManager.getDefaultSensor(sensorType), SensorManager.SENSOR_DELAY_GAME);
            Toast.makeText(this,"Sensors On", Toast.LENGTH_SHORT).show();
            return true;
        }
    }
    //----------------------------------------------------------------------------------------------
    @Override
    public void onSensorChanged(SensorEvent event) {

        int differenceBetween;
        boolean gravityChanged ,magneticFieldChanged, orientationChanged;
        float[] rotation = new float[9];
        float[] inclination = new float[9];
        float[] newOrientation = new float[3];
        TextView theText;

        gravityChanged = magneticFieldChanged =  false;
        theText = findViewById(R.id.theTextView);

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
            arrayCopyChangeTest(newOrientation,orientation,1);
        }
        //gets difference between where phone is pointing and where home is
        differenceBetween = (int)Math.abs(birthPlaceBearing-orientation[0]);
        //checks if phone is flat
        if (Math.abs(orientation[1]) >
                Float.parseFloat(getResources().getString(R.string.minimum_orientation_change)) ||
                Math.abs(orientation[2]) >
                        Float.parseFloat(getResources().getString(R.string.minimum_orientation_change))) {
            theText.setText("Phone is not flat - no meaningful compass bearing");
        }else{
            myProgressBar.setProgress(differenceBetween);
            theText.setText("Home is at " + birthPlaceBearing + " degrees, you are facing " + Math.round(orientation[0]) + " degrees");
            //beeps if pointing to home and checks if it isnt point home
            if(differenceBetween <= 10 && !alreadyBeeping){
                Toast.makeText(this, "Walk straight to get home",Toast.LENGTH_LONG).show();
                myProgressBar.setProgress(0);
                alreadyBeeping = true;
                beeper.startTone(ToneGenerator.TONE_CDMA_ABBR_ALERT,5000);
            } else if(differenceBetween > 10) {
                alreadyBeeping = false;
            }
        }


    }
    //----------------------------------------------------------------------------------------------
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

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
}
//==================================================================================================