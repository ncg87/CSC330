package com.example.talkingpictures;

import android.Manifest;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Map;

//==================================================================================================
public class MainActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener {
    //----------------------------------------------------------------------------------------------
    private MediaRecorder mediaRecorder;
    private String recordFileName;
    private MediaPlayer player;
    //----------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPermissions.launch(new String[] {Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE});
    }
    public void goOnCreating(boolean havePermission){
        if(havePermission){
            setContentView(R.layout.activity_main);
            recordFileName = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).toString() + "/" + getResources().getString(R.string.audio_file_name);
            mediaRecorder = new android.media.MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.setOutputFile(recordFileName);
            try {
                mediaRecorder.prepare();
            } catch (IOException e) {
                Log.i("AUDIO ERROR","PREPARING RECORDER");
                Toast.makeText(this, "RECORDING FAILED", Toast.LENGTH_SHORT).show();
            }
            mediaRecorder.start();
            startGallery.launch("image/*");
        }else{
            Toast.makeText(this,"Need Permissions",Toast.LENGTH_LONG).show();
            finish();
        }

    }
    //----------------------------------------------------------------------------------------------
    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        Toast.makeText(this,"Recording Finished!!!",Toast.LENGTH_LONG).show();
        finish();
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
    ActivityResultLauncher<String> startGallery = registerForActivityResult(
            new ActivityResultContracts.GetContent(),new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri resultUri) {

                    ImageView theImage;
                    theImage = findViewById(R.id.theImage);

                    if (resultUri != null) {
                        theImage.setImageURI(resultUri);
                        mediaRecorder.stop();
                        mediaRecorder.release();
                        player = new MediaPlayer();
                        player.setOnCompletionListener(MainActivity.this);
                        try {
                            player.setDataSource(recordFileName);
                            player.prepare();
                        } catch (IOException e) {
                            Log.i("AUDIO ERROR","PREPARING TO PLAY");
                            Toast.makeText(getApplicationContext(), "RECORDING FAILED TO PLAY", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        player.start();

                    } else {
                        Toast.makeText(MainActivity.this,"No Photo Selected",Toast.LENGTH_LONG).show();
                        finish();
                    }
                }
            });
    //----------------------------------------------------------------------------------------------

    @Override
    protected void onDestroy() {
        super.onDestroy();
        player.release();
    }
}
//==================================================================================================