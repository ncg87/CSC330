package com.example.chooseanicephoto;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

//==================================================================================================
public class SecondActivity extends AppCompatActivity {
//------------------
    private boolean likeOrDislike;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        Uri myUri;
        myUri = this.getIntent().getParcelableExtra("myUri");
        ((ImageView)findViewById(R.id.selected_picture)).setImageURI(myUri);
    }
    //----------------------------------------------------------------------------------------------
    public void myClickHandler(View view) {
        Intent returnIntent;
        switch (view.getId()) {
            case R.id.like:
                likeOrDislike = true;
                returnIntent = new Intent();
                returnIntent.putExtra("likeOrDislike",likeOrDislike);
                setResult(RESULT_OK,returnIntent);
                finish();
                break;
            case R.id.dislike:
                likeOrDislike = false;
                returnIntent = new Intent();
                returnIntent.putExtra("likeOrDislike",likeOrDislike);
                setResult(RESULT_CANCELED,returnIntent);
                finish();
                break;
            default:
                break;
        }
    }
}