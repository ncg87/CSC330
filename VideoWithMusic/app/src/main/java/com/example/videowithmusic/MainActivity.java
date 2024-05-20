package com.example.videowithmusic;

import android.Manifest;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

//==================================================================================================
public class MainActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener,MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener   {
    //----------------------------------------------------------------------------------------------
    private VideoView videoScreen;
    private MediaPlayer myPlayer;
    //----------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getPermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
    }
    //----------------------------------------------------------------------------------------------
    private void goOnCreating(boolean havePermission) {

        if (havePermission) {
            setContentView(R.layout.activity_main);
            videoScreen = findViewById(R.id.video_view);
            videoScreen.setOnCompletionListener(this);
            myPlayer = MediaPlayer.create(this, R.raw.chin_chin_choo);
            myPlayer.setOnCompletionListener(this);
            myPlayer.setOnErrorListener(this);
            myPlayer.start();
        } else {
            Toast.makeText(this,"Need permission",Toast.LENGTH_LONG).show();
            finish();
        }
    }
    //----------------------------------------------------------------------------------------------
    public void myClickListener(View view){

        MediaController videoController;

        switch (view.getId()){
            case R.id.play:
                if (myPlayer != null && myPlayer.isPlaying()) {
                    myPlayer.pause();
                }
                try {
                    videoController = new MediaController(this);
                    videoController.setAnchorView(videoScreen);
                    videoScreen.setMediaController(videoController);
                    videoScreen.setVideoURI(Uri.parse(getString(R.string.mp4_url)));
                    videoScreen.setVisibility(View.VISIBLE);
                    videoScreen.start();
                } catch (Exception e) {
                    Toast.makeText(this,"Could not play URL",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.pause:
                if (myPlayer != null && !myPlayer.isPlaying()) {
                    myPlayer.start();
                }
                if (videoScreen.isPlaying()) {
                    videoScreen.pause();
                } else {
                    Toast.makeText(this,"Not playing",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.resume:
                if (myPlayer != null && myPlayer.isPlaying()) {
                    myPlayer.pause();
                } else {
                    Toast.makeText(this,"Not playing",Toast.LENGTH_SHORT).show();
                }
                if (!videoScreen.isPlaying()) {
                    videoScreen.start();
                } else {
                    Toast.makeText(this,"Not paused",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.stop:
                finish();
                break;
            default:
                break;
        }

    }
    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        videoScreen.setVisibility(View.INVISIBLE);
        Toast.makeText(this,"Completed and ensured invisible",Toast.LENGTH_SHORT).show();
    }
    //----------------------------------------------------------------------------------------------
    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        return false;
    }
    //----------------------------------------------------------------------------------------------
    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {

    }
    //----------------------------------------------------------------------------------------------
    private ActivityResultLauncher<String> getPermission = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean result) {
                    goOnCreating(result);
                }
            });
    //----------------------------------------------------------------------------------------------
    @Override
    public void onDestroy() {

        super.onDestroy();
        myPlayer.stop();
        myPlayer.release();
        if (videoScreen.isPlaying()) {
            videoScreen.stopPlayback();
        }
    }
    //----------------------------------------------------------------------------------------------
}
//==================================================================================================