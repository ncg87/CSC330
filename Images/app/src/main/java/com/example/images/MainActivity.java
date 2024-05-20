package com.example.images;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ToggleButton;

import com.example.images.R;

//=================================================================================================
public class MainActivity extends AppCompatActivity {
    //-------------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//----This is necessary because Android is stupid ... if you set a onClick then setting the
//----clickable false in the XML it gets turned on in setContentView when the onClick is inflated.
        findViewById(R.id.death).setClickable(false);
    }
    //-------------------------------------------------------------------------------------------------
    public void myClickListener(View view) {

        ImageView theImage;
        CheckBox deathEnabler;
        Button deathButton;

        theImage = findViewById(R.id.happy_sad_image);
        switch (view.getId()) {
            case R.id.radio_happy:
                theImage.setImageResource(R.drawable.happy);
                break;
            case R.id.radio_sad:
                theImage.setImageResource(R.drawable.sad);
                break;
            case R.id.toggle_image:
                if (((ToggleButton)view).isChecked()) {
                    theImage.setVisibility(View.VISIBLE);
                } else {
                    theImage.setVisibility(View.INVISIBLE);
                }
                break;
            case R.id.enable_death:
                deathButton = findViewById(R.id.death);
                deathEnabler = findViewById(R.id.enable_death);
                if (deathEnabler.isChecked()) {
                    deathButton.setClickable(true);
                    deathButton.setForeground(ResourcesCompat.getDrawable(getResources(),
                            R.drawable.death,null));
                    deathEnabler.setText(R.string.death_enabled);
                } else {
                    deathButton.setClickable(false);
                    deathButton.setForeground(ResourcesCompat.getDrawable(getResources(),
                            R.drawable.no_death,null));
                    deathEnabler.setText(R.string.death_disabled);
                }
                break;
            case R.id.death:
                finish();
                break;
            default:
                break;
        }
    }
//-------------------------------------------------------------------------------------------------
}
//=================================================================================================