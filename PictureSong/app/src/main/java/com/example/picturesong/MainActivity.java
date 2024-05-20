package com.example.picturesong;

import android.Manifest;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.os.Bundle;
import android.widget.Toast;

import java.io.IOException;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener {
    //----------------------------------------------------------------------------------------------
    private Cursor audioCursor;
    private Cursor mediaCursor;
    private MediaPlayer thePlayer;
    //----------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPermissions.launch(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE});
    }
    //----------------------------------------------------------------------------------------------
    public void goOnCreating(boolean havePermission){
        String[]queryFields = {MediaStore.Audio.Media.DATA};
        if(havePermission){
            setContentView(R.layout.activity_main);
            mediaCursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, queryFields,null,null,MediaStore.Images.Media.DEFAULT_SORT_ORDER);
            audioCursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, queryFields,null,null,MediaStore.Audio.Media.TITLE + " ASC");
            thePlayer = new MediaPlayer();
            thePlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            thePlayer.setOnCompletionListener(this);

            displayImageAtCursor();
            playAudioAtCursor();
        }else{
            Toast.makeText(this,"Need Permissions",Toast.LENGTH_LONG).show();
            finish();
        }
    }
    //----------------------------------------------------------------------------------------------
    public void displayImageAtCursor(){
        ImageView theImage;
        Uri imageUri;
        int idIndex;
        int randomImage;

        theImage = findViewById(R.id.theImage);
        randomImage = (int)((mediaCursor.getCount()) * Math.random() + 1);

        idIndex = mediaCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        mediaCursor.move(randomImage);
        imageUri = Uri.parse(mediaCursor.getString(idIndex));
        if(imageUri != null){
            theImage.setImageURI(imageUri);
        }else{
            Toast.makeText(this,"Can't get image",Toast.LENGTH_SHORT).show();
        }
    }
    //----------------------------------------------------------------------------------------------
    public void playAudioAtCursor(){
        String audioFileName;
        int audioDataIndex;
        int randomAudio;

        try{
        randomAudio = (int)(audioCursor.getCount() * Math.random() + 1);
        audioDataIndex = audioCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        audioCursor.move(randomAudio);
        audioFileName = audioCursor.getString(audioDataIndex);
        thePlayer.setDataSource(audioFileName);
        thePlayer.prepare();
        thePlayer.start();
        }catch (IOException e) {

        }
    }
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
    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        Toast.makeText(this,"Audio Finished!!!",Toast.LENGTH_LONG).show();
        finish();
    }
    //----------------------------------------------------------------------------------------------
    @Override
    protected void onDestroy() {
        super.onDestroy();
        audioCursor.close();
        mediaCursor.close();
        thePlayer.release();
    }
    //----------------------------------------------------------------------------------------------
}