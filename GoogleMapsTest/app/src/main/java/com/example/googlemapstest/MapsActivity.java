package com.example.googlemapstest;

import android.Manifest;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Map;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.FragmentActivity;
//=================================================================================================
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    //-------------------------------------------------------------------------------------------------
    private GoogleMap myMap;
    //-------------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getPermissions.launch(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION});
    }
    //-------------------------------------------------------------------------------------------------
    private void goOnCreating(boolean havePermission) {

        SupportMapFragment mapFragment;

        if (havePermission) {
            setContentView(R.layout.activity_maps);
//----Obtain the SupportMapFragment and get notified when the map is ready to be used.
            mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(
                    R.id.the_map);
            mapFragment.getMapAsync(this);
        } else {
            Toast.makeText(this,"Need permission",Toast.LENGTH_LONG).show();
            finish();
        }
    }
    //-------------------------------------------------------------------------------------------------
//----This callback is triggered when the map is ready to be used.
//----If Google Play services is not installed on the device, the user will be prompted to install
//----it inside the SupportMapFragment. This method will only be triggered once the user has
//----installed Google Play services and returned to the app.
    @Override
    public void onMapReady(GoogleMap googleMap) {

        LatLng uM;
        myMap = googleMap;

        myMap.getUiSettings().setZoomControlsEnabled(true);
//----Add a marker in UM and move the camera
        uM = new LatLng(25.721428,-80.279962);
        myMap.addMarker(new MarkerOptions().position(uM).title("UM"));
        myMap.moveCamera(CameraUpdateFactory.newLatLng(uM));
    }
    //-------------------------------------------------------------------------------------------------
    public void myClickHandler(View view) {

        switch (view.getId()) {
            case R.id.show_map:
                myMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case R.id.show_satallite:
                myMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case R.id.show_hybrid:
                myMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
            default:
                break;
        }
    }
    //-------------------------------------------------------------------------------------------------
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
//-------------------------------------------------------------------------------------------------
}
//=================================================================================================