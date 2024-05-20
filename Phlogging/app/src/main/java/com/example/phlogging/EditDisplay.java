package com.example.phlogging;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.util.List;

//==================================================================================================
public class EditDisplay extends AppCompatActivity {
    //----------------------------------------------------------------------------------------------
    private static DataSQLiteDatabase phloggingDB;
    private boolean inDatabase;
    private Uri photoUri;
    private String time;
    private ContentValues noteData;
    private Location currentLocation;
    private int updates;
    //----------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_display);

        phloggingDB = new DataSQLiteDatabase(this);

        //gets time and location
        time = this.getIntent().getStringExtra(getResources().getString(R.string.dateKey));
        currentLocation = this.getIntent().getParcelableExtra(getResources().getString(R.string.locationKey));

        //checks if in database
        noteData = phloggingDB.getDataByTime(time);
        inDatabase = noteData != null;


        if(inDatabase) {
            setEditNote();
        }else{
            setNewNote();
        }
    }
    //----------------------------------------------------------------------------------------------
    private void setEditNote(){
        //get all fields
        EditText titleText = findViewById(R.id.titleText);
        EditText descriptionText = findViewById(R.id.decriptionText);
        TextView dateAndTime = findViewById(R.id.date);
        TextView longAndLat = findViewById(R.id.location);
        TextView address = findViewById(R.id.address);
        ImageView editImage = findViewById(R.id.theImage);
        //populate fields with data
        updates = noteData.getAsInteger("updates") + 1;
        titleText.setText((String)noteData.get("title"));
        descriptionText.setText((String)noteData.get("description"));
        dateAndTime.setText((String)noteData.get("time"));
        longAndLat.setText("Latitude: " + noteData.get("latitude") + " Longitude: " + noteData.get("longitude"));
        address.setText((String)noteData.get("address"));
        //check if uri is null
        if(noteData.getAsString("photoUri") != null) {
            Uri uri = Uri.parse(noteData.getAsString("photoUri"));
            editImage.setImageURI(uri);
        }
    }
    //----------------------------------------------------------------------------------------------
    private void setNewNote(){
        //If not in db initialize a new contentvalue
        noteData = new ContentValues();
        //gets text views
        TextView dateAndTime = findViewById(R.id.date);
        TextView longAndLat = findViewById(R.id.location);
        TextView address = findViewById(R.id.address);
        //sets # of times updates
        updates = 0;
        //sets time
        dateAndTime.setText(time);

        //formats and sets location and address if received location from previous activity
        if (currentLocation != null) {
            String formattedLocation = "Latitude: " + currentLocation.getLatitude() + " Longitude: " + currentLocation.getLongitude();
            String formattedAddress = androidGeodecode(currentLocation);
            longAndLat.setText(formattedLocation);
            address.setText(formattedAddress);
        }
    }
    //----------------------------------------------------------------------------------------------
    private void saveToContentValue(){
        //get all data points
        EditText titleText = findViewById(R.id.titleText);
        EditText descriptionText = findViewById(R.id.decriptionText);
        TextView addressText = findViewById(R.id.address);
        //Convert to correct data to put into content value
        String title = titleText.getText().toString();
        if(title == null || title.equals("")){
            title = "Untitled Note";
        }
        String description = descriptionText.getText().toString();
        noteData.put("title",title);
        noteData.put("description",description);
        noteData.put("updates",updates);
        //saves photo only if photo was chosen
        if(photoUri != null){
            noteData.put("photoUri",photoUri.toString());
        }
        //if new note then these need to be added otherwise already in contentvalue
        if(!inDatabase) {
            double lat = currentLocation.getLatitude();
            double lng = currentLocation.getLongitude();
            String address = addressText.getText().toString();
            noteData.put("latitude",lat);
            noteData.put("longitude",lng);
            noteData.put("time",time);
            noteData.put("address",address);
        }
    }
    //----------------------------------------------------------------------------------------------
    public void myClickHandler(View view){

        switch (view.getId()){
            /*case R.id.gallery:
                //launch gallery
                startGallery.launch("image/*");
                break;*/
            case R.id.camera:
                //create a file for camera to save photo
                photoUri = FileProvider.getUriForFile(this,getApplicationContext().getPackageName() + ".provider",new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/" + time + updates));
                runCameraPhotoApp.launch(photoUri);
                break;
            case R.id.save:
                saveToContentValue();
                if(inDatabase){
                    phloggingDB.updateData((int) noteData.get("_id"),noteData);
                }else{
                    Toast.makeText(this,"Added entry",Toast.LENGTH_SHORT).show();
                    phloggingDB.addData(noteData);
                }
                setResult(RESULT_OK);
                finish();
                break;
            case R.id.delete:
                if(inDatabase){
                    phloggingDB.deleteData((int) noteData.get("_id"));
                    setResult(RESULT_OK);
                }else{
                    setResult(RESULT_CANCELED);
                }
                finish();
                break;
            default:
                break;
        }
    }
    //----------------------------------------------------------------------------------------------
    private String androidGeodecode(Location thisLocation) {

        Geocoder androidGeocoder;
        List<Address> addresses;
        Address firstAddress;
        String addressLine;
        String locationName;
        int index;

        if (Geocoder.isPresent()) {
            androidGeocoder = new Geocoder(this.getApplicationContext());
            try {
                addresses = androidGeocoder.getFromLocation(thisLocation.getLatitude(),
                        thisLocation.getLongitude(),1);
                if (addresses.isEmpty()) {
                    return("ERROR: Unkown location");
                } else {
                    firstAddress = addresses.get(0);
                    locationName = "";
                    index = 0;
                    while ((addressLine = firstAddress.getAddressLine(index)) != null) {
                        locationName += addressLine + ", ";
                        index++;
                    }
                    return (locationName);
                }
            } catch (Exception e) {
                return("ERROR: " + e.getMessage());
            }
        } else {
            return("ERROR: No Geocoder available");
        }
    }
    //----------------------------------------------------------------------------------------------
    /*ActivityResultLauncher<String> startGallery = registerForActivityResult(

            new ActivityResultContracts.GetContent(),new ActivityResultCallback<Uri>() {

                @Override
                public void onActivityResult(Uri resultUri) {

                    if (resultUri != null) {
                        //grants permanent access to uri
                        getApplicationContext().grantUriPermission(getApplicationContext().getPackageName(), resultUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        //sets imageview to chosen picture
                        ImageView editImage = findViewById(R.id.theImage);
                        editImage.setImageURI(resultUri);
                        //saves uri
                        photoUri = resultUri;
                    }else{
                        Toast.makeText(EditDisplay.this,"No Image Chosen",Toast.LENGTH_SHORT).show();
                    }
                }
            });*/
    //----------------------------------------------------------------------------------------------
    private ActivityResultLauncher<Uri> runCameraPhotoApp = registerForActivityResult(
            new ActivityResultContracts.TakePicture(),new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean result) {

                    if (result) {
                        //sets image to photo taken
                        ImageView editImage = findViewById(R.id.theImage);
                        editImage.setImageURI(photoUri);
                    }else{
                        Toast.makeText(getApplicationContext(), "Photo not chosen!!!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            });
    //----------------------------------------------------------------------------------------------
}
//==================================================================================================