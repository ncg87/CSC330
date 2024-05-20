package com.example.rollanddrop;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

//==================================================================================================
public class MainActivity extends AppCompatActivity {
    //----------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    //----------------------------------------------------------------------------------------------
    public void myClickHandler(View view){

        Animation imageAnimation;
        ImageView imageToAnimate;

        imageToAnimate = findViewById(R.id.theImage);
        imageToAnimate.setAnimation(null);
        if(view.getId() == R.id.theButton){
            imageAnimation = AnimationUtils.loadAnimation(this,R.anim.image_roll_and_drop);
            imageToAnimate.startAnimation(imageAnimation);
        }
    }
}
//==================================================================================================