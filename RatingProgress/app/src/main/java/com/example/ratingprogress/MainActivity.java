package com.example.ratingprogress;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Toast;

//===================================================
public class MainActivity extends AppCompatActivity {
    //------------------------------------------------
    private RatingBar theRatingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        theRatingBar = findViewById(R.id.rating_bar);
        theRatingBar.setRating(0);
    }
    //------------------------------------------------------
    public void myClickListener(View view){
        Intent nextActivity;
        if(view.getId() == R.id.next_activity){
            nextActivity = new Intent(this,Activity2.class);
            progressBar.launch(nextActivity);
        }
    }
    //-----------------------------------------------------
    ActivityResultLauncher<Intent> progressBar = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        theRatingBar.setRating(theRatingBar.getRating()+1);
                        if(theRatingBar.getRating() >= 5){
                            finish();
                            return;
                        }
                    } else {
                        Toast.makeText(MainActivity.this,"NO PROGRESS MADE!",Toast.LENGTH_SHORT).show();
                    }
                }
            });
}