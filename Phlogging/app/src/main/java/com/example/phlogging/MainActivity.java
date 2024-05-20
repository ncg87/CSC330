package com.example.phlogging;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.Priority;

import java.util.Date;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

//==================================================================================================
public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{
    //----------------------------------------------------------------------------------------------
    private static DataSQLiteDatabase phloggingDB;
    private FusedLocationProviderClient fusedLocationClient;
    private Location currentLocation;
    private LocationRequest locationRequest;
    //----------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getPermissions.launch(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.CAMERA});

    }
    //----------------------------------------------------------------------------------------------
    public void goOnCreating(boolean havePermission){

        //if have permissions run app else terminate app
        if(havePermission){

            setContentView(R.layout.activity_main);
            //initialize database and start locating
            phloggingDB = new DataSQLiteDatabase(this);
            //Toast.makeText(this,phloggingDB.fetchAllData().getCount() + " entries",Toast.LENGTH_LONG).show();
            startLocation();
            //only fill list if there is data in the database
            if(phloggingDB.fetchAllData().getCount() > 0) {
                fillList();
            }

        }else{

            Toast.makeText(this,"Need permissions",Toast.LENGTH_LONG).show();
            finish();
        }

    }
    //----------------------------------------------------------------------------------------------
    public void myClickHandler(View view){

        Intent nextActvitiy;

        switch (view.getId()){
            case R.id.phloggingButton:
                //create new intent and put location and time into it
                nextActvitiy = new Intent(MainActivity.this,EditDisplay.class);
                String noteDate = getTime();
                nextActvitiy.putExtra(getResources().getString(R.string.dateKey),noteDate);
                nextActvitiy.putExtra(getResources().getString(R.string.locationKey),currentLocation);
                editDisplay.launch(nextActvitiy);
                break;
            default:
                break;
        }
    }
    //----------------------------------------------------------------------------------------------
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Intent nextActivity = new Intent(MainActivity.this,EditDisplay.class);
        //gets time
        TextView time = view.findViewById(R.id.time);
        String noteDate = time.getText().toString();
        //puts time into intent
        nextActivity.putExtra(getResources().getString(R.string.dateKey),noteDate);
        //launches intent
        editDisplay.launch(nextActivity);

    }
    //----------------------------------------------------------------------------------------------
    public String getTime(){

        long unixTime = System.currentTimeMillis();
        Date itemDate = new Date(unixTime);
        String noteDate = itemDate.toString();
        return noteDate;
    }
    //----------------------------------------------------------------------------------------------
    public void startLocation(){

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
    }
    //----------------------------------------------------------------------------------------------
    public void fillList(){

        String[] displayFields = {"thumbnail","title","time"};
        int[] displayViews = {R.id.thumbnail,R.id.title,R.id.time};
        ArrayList<HashMap<String,Object>> databaseList = new ArrayList<>();

        ListView theList;
        SimpleAdapter listAdapter;
        HashMap<String,Object> singleItem;
        ContentValues singleContent = null;

        Cursor db = phloggingDB.fetchAllData();
        db.moveToFirst();
        int dbColumnId = db.getColumnIndexOrThrow("_id");
        int noteIdColumn;
        //checks if database is empty
        if(db.getCount() <= 0){
            //makes list empty
            theList = findViewById(R.id.theList);
            listAdapter = new SimpleAdapter(this,databaseList,R.layout.list_cell,displayFields,displayViews);
            theList.setAdapter(listAdapter);
            return;
        }

        //populates list only if not empty
        do {
            //creates a single item to put in list
            singleItem = new HashMap<>();
            //gets content of each database entry
            noteIdColumn = db.getInt(dbColumnId);
            singleContent = phloggingDB.getDataById(noteIdColumn);
            //puts thumbnail in if not null
            if(singleContent.get("photoUri") != null){
                Uri uri = Uri.parse((String)singleContent.get("photoUri"));
                //grantUriPermission(getPackageName(), uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                singleItem.put("thumbnail", uri);
            }else{
                singleItem.put("thumbnail", (String)null);
            }
            // puts title, and time in hashmap
            singleItem.put("title", singleContent.get("title"));
            singleItem.put("time", singleContent.get("time"));
            //add hashmap to arraylist
            databaseList.add(singleItem);
        } while (db.moveToNext());
        //populates list with arraylist
        theList = findViewById(R.id.theList);
        listAdapter = new SimpleAdapter(this,databaseList,R.layout.list_cell,displayFields,displayViews);
        //sets thumbnail picture
        listAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data, String textRepresentation) {
                if(view instanceof ImageView & data instanceof Uri){
                    ImageView imageView = (ImageView) view;
                    Uri photoUri = (Uri)data;
                    if(photoUri != null) {
                        imageView.setImageURI(photoUri);
                        return true;
                    }
                }
                return false;
            }
        });

        theList.setAdapter(listAdapter);
        theList.setOnItemClickListener(this);

    }
    //----------------------------------------------------------------------------------------------
    LocationCallback myLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {

            if (locationResult == null || locationResult.getLastLocation() == null) {
                Toast.makeText(MainActivity.this, "No location", Toast.LENGTH_SHORT).show();
                return;
            }
            currentLocation = locationResult.getLastLocation();
        }
    };
    //----------------------------------------------------------------------------------------------
    ActivityResultLauncher<Intent> editDisplay = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK ){
                        fillList();
                    }
                }
            });
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
    public void onDestroy(){

        super.onDestroy();
        phloggingDB.close();
        fusedLocationClient.removeLocationUpdates(myLocationCallback);

    }
    //----------------------------------------------------------------------------------------------
}
//==================================================================================================