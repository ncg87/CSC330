package com.example.stillmoving;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import java.net.URI;
//==================================================================================================

public class runDisplay extends AppCompatActivity implements MediaPlayer.OnCompletionListener{
    //----------------------------------------------------------------------------------------------
    private Uri photoUri;
    private Uri videoUri;
    //----------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_display);

        VideoView theVideo;
        theVideo = (VideoView) findViewById(R.id.theVideo);
        ImageView theImage;
        theImage = findViewById(R.id.theImage);

        photoUri = this.getIntent().getParcelableExtra("photoUri");
        videoUri = this.getIntent().getParcelableExtra("videoUri");

        theImage.setImageURI(photoUri);
        theVideo.setVideoURI(videoUri);

        theVideo.setOnCompletionListener(this);
        theVideo.start();

    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        Toast.makeText(this, "Video Completed!!",Toast.LENGTH_SHORT).show();
        finish();
    }
    //----------------------------------------------------------------------------------------------
}
//==================================================================================================