package com.example.ratingprogress;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ProgressBar;

//==============================================================
public class Activity2 extends AppCompatActivity {
private ProgressBar myProgressBar;
private int barClickTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2);
        myProgressBar = findViewById(R.id.progress_bar);
        myProgressBar.setProgress(myProgressBar.getMax());
        barClickTime = getResources().getInteger(R.integer.bar_count_time);
        myProgressor.run();
    }
    //---------------------------------------------------------
    private Handler myHandler = new Handler();
    private final Runnable myProgressor = new Runnable() {
        Intent returnIntent;
        @Override
        public void run() {
            myProgressBar.setProgress(myProgressBar.getProgress()-barClickTime);
            if(myProgressBar.getProgress() <= 0){
                returnIntent = new Intent();
                setResult(RESULT_OK,returnIntent);
                finish();
            }
            if(!myHandler.postDelayed(myProgressor,barClickTime)){
                Log.e("ERROR","Cannot postDelayed");
            }
        }
    };
    //-------------------------------------------------------
    protected void onDestroy() {

        super.onDestroy();
        myHandler.removeCallbacks(myProgressor);
    }
}