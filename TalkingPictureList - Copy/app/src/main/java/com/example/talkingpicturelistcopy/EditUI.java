package com.example.talkingpicturelistcopy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.view.View;
import com.example.talkingpicturelist.R;

//==================================================================================================
public class EditUI extends AppCompatActivity {
    //----------------------------------------------------------------------------------------------
    private int imageId;
    //----------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_ui);

        String imageUri = this.getIntent().getStringExtra(getResources().getString(R.string.imageUriKey));
        imageId = this.getIntent().getIntExtra(getResources().getString(R.string.imageIdKey),0);
        ImageView theImage = findViewById(R.id.theEditImage);
        theImage.setImageURI(Uri.parse(imageUri));
    }
    //----------------------------------------------------------------------------------------------
    public void myClickHandler(View view){

        String newDesciption = ((EditText) findViewById(R.id.theEdit)).getText().toString();

        switch(view.getId()){
            case R.id.saveButton:
                Intent returnIntent = new Intent();
                if(!newDesciption.equals("") && newDesciption != null){
                    returnIntent.putExtra(getResources().getString(R.string.newDescriptionKey),newDesciption);
                    returnIntent.putExtra(getResources().getString(R.string.imageIdKey),imageId);
                    setResult(RESULT_OK,returnIntent);
                }else{
                    setResult(RESULT_CANCELED);
                }
                finish();
                break;
            default:
                break;
        }
    }
}
//==================================================================================================