package com.example.stillmoving;

import android.Manifest;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.util.Map;
//==================================================================================================
public class MainActivity extends AppCompatActivity {

    //----------------------------------------------------------------------------------------------
    private Uri photoUri;
    private Uri videoUri;
    private Intent nextActivity;
    //----------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPermissions.launch(new String[] {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE});
    }
    //----------------------------------------------------------------------------------------------
    public void goOnCreating(boolean havePermission){
        if(havePermission){
            setContentView(R.layout.activity_main);
            photoUri = FileProvider.getUriForFile(this,getApplicationContext().getPackageName() + ".provider",new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/" + getResources().getString(R.string.camera_photo_name)));
            videoUri = FileProvider.getUriForFile(this,getApplicationContext().getPackageName() + ".provider",new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).toString() + "/" + getResources().getString(R.string.camera_video_name)));
            nextActivity = new Intent(this,runDisplay.class);
            runCameraPhotoApp.launch(photoUri);
        }else{
            Toast.makeText(this,"Need Permissions",Toast.LENGTH_LONG).show();
            finish();
        }
    }
    //----------------------------------------------------------------------------------------------
    private ActivityResultLauncher<Uri> runCameraPhotoApp = registerForActivityResult(
            new ActivityResultContracts.TakePicture(),new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean result) {

                    if (result) {
                        nextActivity.putExtra("photoUri",photoUri);
                        runCameraVideoApp.launch(videoUri);
                    }else{
                        Toast.makeText(getApplicationContext(), "Photo not chosen!!!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            });
    //----------------------------------------------------------------------------------------------
    private ActivityResultLauncher<Uri> runCameraVideoApp = registerForActivityResult(
            new ActivityResultContracts.TakeVideo(),new ActivityResultCallback<Bitmap>() {
                @Override
                public void onActivityResult(Bitmap result) {
                    nextActivity.putExtra("videoUri", videoUri);
                    runDisplay.launch(nextActivity);
                }
            });
    //----------------------------------------------------------------------------------------------
    private ActivityResultLauncher<Intent> runDisplay = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    finish();
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
}
//==================================================================================================