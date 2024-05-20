package com.example.whereami;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.Priority;

import android.app.Activity;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.Map;

//==================================================================================================
public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {
    //----------------------------------------------------------------------------------------------
    private FusedLocationProviderClient fusedLocationClient;
    private Location currentLocation;
    private Location lastLocation;
    private LocationRequest locationRequest;
    public TextToSpeech mySpeaker;
    public Activity activity;
    public boolean isSpeakingLocation = false;

    //----------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getPermissions.launch(new String[] {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION});

    }
    //----------------------------------------------------------------------------------------------
    public void goOnCreating(boolean havePermission){

            //if have permissions run app else terminate app
        if(havePermission){
            setContentView(R.layout.activity_main);
            activity = MainActivity.this;
            mySpeaker = new TextToSpeech(this,this);
        }else{
            Toast.makeText(this,"Need permission",Toast.LENGTH_LONG).show();
            finish();
        }

    }
    //----------------------------------------------------------------------------------------------
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            mySpeaker.setOnUtteranceProgressListener(myListener);
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
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
        } else {
            Toast.makeText(this,"TTS could not be initialized",Toast.LENGTH_LONG).show();
            finish();
        }
    }
    //----------------------------------------------------------------------------------------------
    LocationCallback myLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {

            if (locationResult == null || locationResult.getLastLocation() == null) {
                Toast.makeText(MainActivity.this,"No location",Toast.LENGTH_SHORT).show();
                return;
            }
            currentLocation = locationResult.getLastLocation();

            if (lastLocation == null || currentLocation != null && currentLocation.distanceTo(lastLocation) > currentLocation.getAccuracy()) {
                new SensorLocatorDecode(activity,mySpeaker).execute(currentLocation);
                lastLocation = currentLocation;
                isSpeakingLocation = true;
            } else if (!isSpeakingLocation) {
                    mySpeaker.speak(getString(R.string.no_movement),TextToSpeech.QUEUE_ADD,null,"WHAT_I_SAID");
            }
        }
    };
    //----------------------------------------------------------------------------------------------
    //Prompts to allow for permissions
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
    public UtteranceProgressListener myListener = new UtteranceProgressListener() {
        @Override
        public void onStart(String utteranceId) {

        }

        @Override
        public void onDone(String utteranceId) {
            isSpeakingLocation = false;
        }

        @Override
        public void onError(String utteranceId) {
        }
    };
    //----------------------------------------------------------------------------------------------
    @Override
    public void onDestroy() {

        super.onDestroy();
        fusedLocationClient.removeLocationUpdates(myLocationCallback);
        mySpeaker.shutdown();

    }
    //----------------------------------------------------------------------------------------------
}
//==================================================================================================