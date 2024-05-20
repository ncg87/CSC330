package com.example.chooseanicephoto;

import androidx.activity.result.ActivityResult;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

//===========================================================
public class MainActivity extends AppCompatActivity {
//------------------------------------------------------
    private String imageSelected;
//-------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startGallery.launch("image/*");
    }
//-------------------------------------------------------
//---------------------------------------------------------------------
    ActivityResultLauncher<String> startGallery = registerForActivityResult(
            new ActivityResultContracts.GetContent(),new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri resultUri) {

                    if (resultUri != null) {
                        Intent nextActivity;
                        nextActivity = new Intent(MainActivity.this,SecondActivity.class);
                        nextActivity.putExtra("myUri",resultUri);
                        photoarl.launch(nextActivity);
                    } else {
                        Toast.makeText(MainActivity.this,"You are a wimp!!",Toast.LENGTH_LONG).show();
                        finish();
                    }
                }
            });

//----------------------------------------------------------------------
    ActivityResultLauncher<Intent> photoarl = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Toast.makeText(MainActivity.this,"Good Picture!!",Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        startGallery.launch("image/*");
                    }
                }
            });
//-------------------------------------------------------------
}
//================================================================