package com.example.uinon_containerelements;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RatingBar;

import com.example.uinon_containerelements.R;

//=================================================================================================
public class MainActivity extends AppCompatActivity {
    //-------------------------------------------------------------------------------------------------
    private ProgressBar myProgressBar;
    private RatingBar theRatingBar;
    private int barClickTime;
    //-------------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        theRatingBar = findViewById(R.id.score);
        theRatingBar.setRating(theRatingBar.getNumStars());

        myProgressBar = findViewById(R.id.time_left);
        myProgressBar.setProgress(myProgressBar.getMax());
        barClickTime = getResources().getInteger(R.integer.bar_click_time);
    }
    //-------------------------------------------------------------------------------------------------
    public void myClickHandler(View view) {

        switch (view.getId()) {
            case R.id.start_button:
                findViewById(R.id.start_button).setClickable(false);
                myProgresser.run();
                break;
            default:
                break;
        }
    }
    //-------------------------------------------------------------------------------------------------
    private Handler myHandler = new Handler();
    private final Runnable myProgresser = new Runnable() {

        public void run() {

            myProgressBar.setProgress(myProgressBar.getProgress()-barClickTime);
            if (myProgressBar.getProgress() <= 0) {
                theRatingBar.setRating(theRatingBar.getRating()-1);
                if (theRatingBar.getRating() <= 0) {
                    finish();
                    return;
                } else {
                    myProgressBar.setProgress(myProgressBar.getMax());
                }
            }
            if (!myHandler.postDelayed(myProgresser,barClickTime)) {
                Log.e("ERROR","Cannot postDelayed");
            }
        }
    };
    //-------------------------------------------------------------------------------------------------
    @Override
    protected void onDestroy() {

        super.onDestroy();
        myHandler.removeCallbacks(myProgresser);
    }
//-------------------------------------------------------------------------------------------------
}
//=================================================================================================