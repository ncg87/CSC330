package com.example.imagecursor;

import android.Manifest;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Map;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

//=================================================================================================
public class MainActivity extends AppCompatActivity {
    //-------------------------------------------------------------------------------------------------
    private Cursor imagesCursor;
    //-------------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getPermissions.launch(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE});
    }
    //-------------------------------------------------------------------------------------------------
    private void goOnCreating(boolean havePermission) {

        String[] queryFields = {
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATA
        };

        if (havePermission) {
            setContentView(R.layout.activity_main);
            imagesCursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    queryFields,null,null,MediaStore.Images.Media.DEFAULT_SORT_ORDER);
            if (imagesCursor.moveToFirst()) {
                displayImageAtCursor();
            } else {
                finish();
            }
        } else {
            Toast.makeText(this,"Need permission",Toast.LENGTH_LONG).show();
            finish();
        }
    }
    //-------------------------------------------------------------------------------------------------
    public void myClickHandler(View view) {

        switch (view.getId()) {
            case R.id.previous:
                if (!imagesCursor.moveToPrevious()) {
                    imagesCursor.moveToLast();
                }
                break;
            case R.id.next:
                if (!imagesCursor.moveToNext()) {
                    imagesCursor.moveToFirst();
                }
                break;
            default:
                break;
        }
        displayImageAtCursor();
    }
    //-------------------------------------------------------------------------------------------------
    private void displayImageAtCursor() {

        ImageView thumbnailView;
        ImageView fullView;
        int idIndex;
        int dataIndex;
        Bitmap thumbnailBitmap;
        Uri fullImageURI;

        thumbnailView = findViewById(R.id.current_thumbnail);
        thumbnailView.setImageResource(android.R.color.transparent);
        idIndex = imagesCursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
        thumbnailBitmap = MediaStore.Images.Thumbnails.getThumbnail(getContentResolver(),
                imagesCursor.getInt(idIndex),MediaStore.Images.Thumbnails.MICRO_KIND,null);
        if (thumbnailBitmap != null) {
            thumbnailView.setImageBitmap(thumbnailBitmap);
        } else {
            Toast.makeText(this,"Can't get thumbnail",Toast.LENGTH_SHORT).show();
        }

        fullView = findViewById(R.id.current_picture);
        fullView.setImageResource(android.R.color.transparent);
        dataIndex = imagesCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        fullImageURI = Uri.parse(imagesCursor.getString(dataIndex));
        if (fullImageURI != null) {
            fullView.setImageURI(fullImageURI);
        } else {
            Toast.makeText(this,"Can't get image",Toast.LENGTH_SHORT).show();
        }
    }

    //-------------------------------------------------------------------------------------------------
    private ActivityResultLauncher<String[]> getPermissions = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            new ActivityResultCallback<Map<String, Boolean>>() {
                @Override
                public void onActivityResult(Map<String, Boolean> results) {

                    for (String key:results.keySet()) {
                        if (!results.get(key)) {
                            goOnCreating(false);
                        }
                    }
                    goOnCreating(true);
                }
            });
    //-------------------------------------------------------------------------------------------------
    @Override
    public void onDestroy() {

        super.onDestroy();
        imagesCursor.close();
    }
//-------------------------------------------------------------------------------------------------
}
//=================================================================================================