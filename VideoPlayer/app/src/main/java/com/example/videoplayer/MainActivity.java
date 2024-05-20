package com.example.videoplayer;

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

//=================================================================================================
public class MainActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener {
    //-------------------------------------------------------------------------------------------------
    private VideoView videoScreen;
    //-------------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getPermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
    }
    //-------------------------------------------------------------------------------------------------
    private void goOnCreating(boolean havePermission) {

        if (havePermission) {
            setContentView(R.layout.activity_main);
            videoScreen = findViewById(R.id.video_screen);
            videoScreen.setOnCompletionListener(this);
        } else {
            Toast.makeText(this,"Need permission",Toast.LENGTH_LONG).show();
            finish();
        }
    }
    //-------------------------------------------------------------------------------------------------
    public void myClickListener(View view) {

        MediaController videoController;

        switch (view.getId()) {
            case R.id.video_file_play:
                try {
                    videoScreen.setVideoPath(Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_MOVIES).toString() + "/" + getString(R.string.movie_file_name));
                    videoScreen.setVisibility(View.VISIBLE);
                    videoScreen.start();
                } catch (Exception e) {
                    Toast.makeText(this,"Could not play file",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.video_url_play:
                try {
//----Add a regular controller, for an example
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
            case R.id.video_pause:
                if (videoScreen.isPlaying()) {
                    videoScreen.pause();
                } else {
                    Toast.makeText(this,"Not playing",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.video_resume:
                if (!videoScreen.isPlaying()) {
//----The touted resume() function does not work
                    videoScreen.start();
                } else {
                    Toast.makeText(this,"Not paused",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.video_stop:
                if (videoScreen.isPlaying()) {
                    videoScreen.stopPlayback();
                    videoScreen.setVisibility(View.INVISIBLE);
                    Toast.makeText(this,"Stopped and made invisible",Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this,"Not playing or paused",Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.exit:
                finish();
                break;
            default:
                break;
        }
    }
    //-------------------------------------------------------------------------------------------------
    public void onCompletion(MediaPlayer player) {

        videoScreen.setVisibility(View.INVISIBLE);
        Toast.makeText(this,"Completed and ensured invisible",Toast.LENGTH_SHORT).show();
    }
    //-------------------------------------------------------------------------------------------------
    private ActivityResultLauncher<String> getPermission = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean result) {
                    goOnCreating(result);
                }
            });
    //-------------------------------------------------------------------------------------------------
    @Override
    public void onDestroy() {

        super.onDestroy();
        if (videoScreen.isPlaying()) {
            videoScreen.stopPlayback();
        }
    }
//-------------------------------------------------------------------------------------------------
}
//=================================================================================================