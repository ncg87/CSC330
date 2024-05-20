package com.example.adapterlist;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private Cursor imageCursor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPermissions.launch(new String[] {android.Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE});
    }
    public void goOnCreating(boolean havePermission) {

        String[] imageQueryFields = {
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATA
        };

        if (havePermission) {
            setContentView(R.layout.activity_main);
            imageCursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    imageQueryFields, null, null, MediaStore.Images.Media.DEFAULT_SORT_ORDER);
            fillList();
        } else {
            finish();
        }
    }

    private void fillList() {
        imageCursor.moveToFirst();
        SimpleAdapter adapter;
        String[] displayFields = {"_id","thumbnail"};
        int[] displayViews = {R.id._id,R.id.thumbnail};
        ListView theList = findViewById(R.id.theList);
        ArrayList<HashMap<String,Object>> databaseList = new ArrayList<>();
        HashMap<String,Object> singleItem;
        Bitmap thumbnailBitmap;
        int idIndex;
        int index = 0;
        do{
            singleItem = new HashMap<>();
            idIndex = imageCursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
            thumbnailBitmap = MediaStore.Images.Thumbnails.getThumbnail(getContentResolver(),
                    imageCursor.getInt(idIndex),MediaStore.Images.Thumbnails.MICRO_KIND,null);
            Toast.makeText(this,idIndex+"",Toast.LENGTH_LONG).show();
            singleItem.put("_id",index);
            singleItem.put("thumbnail",thumbnailBitmap);

            databaseList.add(singleItem);
            ++index;

        }while(imageCursor.moveToNext());

        adapter = new SimpleAdapter(this,databaseList,R.layout.list_item,displayFields,displayViews);
        adapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data, String textRepresentation) {
                if ( (view instanceof ImageView) & data instanceof  Bitmap ){
                    ImageView iv = (ImageView) view;
                    Bitmap bm = (Bitmap) data;
                    iv.setImageBitmap(bm);
                    return true;
                }
                return false;
            }
        });
        theList.setAdapter(adapter);
    }

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
}