package com.example.buttons;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.buttons.R;

//=================================================================================================
public class MainActivity extends AppCompatActivity {
    //-------------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    //-------------------------------------------------------------------------------------------------
    public void myClickHandler(View view) {

        TextView theText;

        switch (view.getId()) {
            case R.id.hide_show:
               /* theText = findViewById(R.id.the_text);
                if (theText.getVisibility() == View.VISIBLE) {
                    theText.setVisibility(View.INVISIBLE);
                } else {
                    theText.setVisibility(View.VISIBLE);
                }*/
                break;
            default:
                break;
        }
    }
//-------------------------------------------------------------------------------------------------
}
//=================================================================================================