package com.example.audioplayer;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;

import androidx.appcompat.app.AppCompatActivity;

//=================================================================================================
public class MainActivity extends AppCompatActivity implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener,MediaPlayer.OnErrorListener {
    //-------------------------------------------------------------------------------------------------
    private MediaPlayer myPlayer = null;
    //-------------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    //-------------------------------------------------------------------------------------------------
    public void myClickListener(View view) {

        switch (view.getId()) {
            case R.id.audio_raw_play:
                if (myPlayer == null) {
                    myPlayer = MediaPlayer.create(this, R.raw.chin_chin_choo);
                    myPlayer.setOnCompletionListener(this);
                    myPlayer.setOnErrorListener(this);
                    myPlayer.start();
                } else {
                    Toast.makeText(this,"Already playing",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.audio_url_play:
                if (myPlayer == null) {
                    myPlayer = new MediaPlayer();
                    myPlayer.setOnPreparedListener(this);
                    myPlayer.setOnCompletionListener(this);
                    myPlayer.setOnErrorListener(this);
                    myPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    try {
                        myPlayer.setDataSource(getString(R.string.mp3_url));
                        myPlayer.prepareAsync();
                    } catch (IOException e) {
                        Toast.makeText(this,"URL not available",Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this,"Already playing",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.audio_pause:
                if (myPlayer != null && myPlayer.isPlaying()) {
                    myPlayer.pause();
                } else {
                    Toast.makeText(this,"Not playing",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.audio_resume:
                if (myPlayer != null && !myPlayer.isPlaying()) {
                    myPlayer.start();
                } else {
                    Toast.makeText(this,"Not paused",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.audio_stop:
                if (myPlayer != null) {
                    myPlayer.stop();
                    myPlayer.release();
                    myPlayer = null;
                    Toast.makeText(this,"Stopped and released",Toast.LENGTH_SHORT).show();
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
    public void onPrepared(MediaPlayer mediaPlayer) {

        mediaPlayer.start();
    }
    //-------------------------------------------------------------------------------------------------
    public void onCompletion(MediaPlayer mediaPlayer) {

        mediaPlayer.release();
        myPlayer = null;
    }
    //-------------------------------------------------------------------------------------------------
    public boolean onError(MediaPlayer mediaPlayer,int whatHappened,int extra) {

        Toast.makeText(this, "Media player error " + whatHappened + " " + extra, Toast.LENGTH_LONG).
                show();
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        mediaPlayer.release();
        myPlayer = null;
        return(true);
    }
    //-------------------------------------------------------------------------------------------------
    @Override
    public void onDestroy() {

        super.onDestroy();
        myPlayer.stop();
        myPlayer.release();
    }
//-------------------------------------------------------------------------------------------------
}
//=================================================================================================