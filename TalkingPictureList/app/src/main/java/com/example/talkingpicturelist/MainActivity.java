package com.example.talkingpicturelist;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.content.DialogInterface;
import android.app.DialogFragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//==================================================================================================
public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener,TextToSpeech.OnInitListener,UIDialogsCustomFragment.StopTalking, MediaPlayer.OnCompletionListener{
    //----------------------------------------------------------------------------------------------
    private static DataSQLiteDataBase imageDB;
    private Cursor audioCursor;
    private Cursor imageCursor;
    public TextToSpeech mySpeaker;
    private MediaPlayer thePlayer;
    //----------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPermissions.launch(new String[] {android.Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE});
    }
    //----------------------------------------------------------------------------------------------
    public void goOnCreating(boolean havePermission){


        if (havePermission){
            setContentView(R.layout.activity_main);
            assignObjects();
            playAudioAtCursor();
            if(imageCursor != null && imageCursor.getCount() > 0){
                updateImageDBFromContent();
                fillList();
            }
        }else{
            finish();
        }
    }
    //----------------------------------------------------------------------------------------------
    private void updateImageDBFromContent(){

        ContentValues imageData;
        int imageMediaId;
        int imageId;

        imageCursor.moveToFirst();
        if(imageCursor != null) {
            do {
                imageMediaId = imageCursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
                imageId = imageCursor.getInt(imageMediaId);
                //If the picture is not in the DB, add it
                if (imageDB.getDataByImageId(imageId) == null) {
                    imageData = new ContentValues();
                    imageData.put("imageId", imageId);
                    imageData.put("description", getResources().getString(R.string.base_text));
                    Log.i("DATABASE",imageId + " put into Database");
                    if (!imageDB.addData(imageData)) {
                        Toast.makeText(this, "Error adding to DB", Toast.LENGTH_LONG).show();
                    }
                }
            } while (imageCursor.moveToNext());
        }else{
            Toast.makeText(this,"No Photos",Toast.LENGTH_LONG).show();
            finish();
        }
    }
    //----------------------------------------------------------------------------------------------
    private void fillList(){
        //resets cursor position
        imageCursor.moveToFirst();
        //assigning fields
        String[] displayFields = {"thumbnail","description"};
        int[] displayViews = {R.id.thumbnail,R.id.description};
        ArrayList<HashMap<String,Object>> databaseList = new ArrayList<>();

        ListView theList;
        SimpleAdapter listAdapter;
        HashMap<String,Object> singleItem;
        ContentValues singleContent;
        int imageId;
        int imageMediaId;

        //go through images and add to array list
        do{
            //create a single item to put in list
            singleItem = new HashMap<>();
            //get id of image to get thumbnail
            imageMediaId = imageCursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
            imageId = imageCursor.getInt(imageMediaId);
            singleContent = imageDB.getDataByImageId(imageId);
            //get thumbnail and description of picture
            singleItem.put("thumbnail",MediaStore.Images.Thumbnails.getThumbnail(getContentResolver(),
                    imageCursor.getInt(imageMediaId),MediaStore.Images.Thumbnails.MICRO_KIND,null));
            singleItem.put("description",singleContent.get("description"));

            //add hashmap with thumbnail and description to arraylist
            databaseList.add(singleItem);

        }while(imageCursor.moveToNext());

        //put arraylist into list
        theList = findViewById(R.id.theList);
        listAdapter = new SimpleAdapter(this,databaseList,R.layout.item_list,displayFields,displayViews);
        //sets thumbnail picture
        listAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data, String textRepresentation) {
                if((view instanceof ImageView) & data instanceof Bitmap){
                    ImageView iv = (ImageView) view;
                Bitmap bm = (Bitmap) data;
                iv.setImageBitmap(bm);
                return true;
            }
                return false;
            }
        });
        theList.setAdapter(listAdapter);
        theList.setOnItemClickListener(this);
    }
    //----------------------------------------------------------------------------------------------
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
    //----------------------------------------------------------------------------------------------
    public void onDestroy(){

        super.onDestroy();
        imageDB.close();
        mySpeaker.shutdown();
        thePlayer.release();
    }
    //----------------------------------------------------------------------------------------------
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        //gets image description
        TextView description = view.findViewById(R.id.description);
        //resets cursor position
        imageCursor.moveToFirst();
        //moves cursor to image
        imageCursor.move(position);
        //gets image id
        int imageMediaId = imageCursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
        int imageId = imageCursor.getInt(imageMediaId);
        //gets image uri
        int idIndex = imageCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        String imageUri = imageCursor.getString(idIndex);

        //check to see if description is base text
        if(!description.getText().toString().equals(getResources().getString(R.string.base_text))) {

            Bundle bundleToFragment;
            UIDialogsCustomFragment customFragment;
            ContentValues itemData;

            bundleToFragment = new Bundle();
            customFragment = new UIDialogsCustomFragment();
            itemData = imageDB.getDataByImageId(imageId);


            if (itemData != null) {
                bundleToFragment.putString(getResources().getString(R.string.imageUriKey), imageUri);
                customFragment.setArguments(bundleToFragment);
                customFragment.show(getFragmentManager(), "my_fragment");
                thePlayer.pause();
                mySpeaker.speak(itemData.get("description").toString(), TextToSpeech.QUEUE_ADD, null, "description");
            }
        }else{
            //launch edit activity
            Intent nextActivity;
            nextActivity = new Intent(MainActivity.this,EditUI.class);
            //send image uri and image id to next activity
            nextActivity.putExtra(getResources().getString(R.string.imageUriKey),imageUri);
            nextActivity.putExtra(getResources().getString(R.string.imageIdKey),imageId);
            editArl.launch(nextActivity);
            //pauses song
            thePlayer.pause();
        }

    }
    //----------------------------------------------------------------------------------------------
    @Override
    public void onInit(int status) {

        if(status == TextToSpeech.SUCCESS){
            mySpeaker.setOnUtteranceProgressListener(myListener);
        }else{
            Toast.makeText(this,"TTS could not be initialized",Toast.LENGTH_LONG).show();
            finish();
        }
    }
    //----------------------------------------------------------------------------------------------
    public UtteranceProgressListener myListener = new UtteranceProgressListener() {
        @Override
        public void onStart(String utteranceId) {

        }

        @Override
        public void onDone(String utteranceId) {

        }

        @Override
        public void onError(String utteranceId) {
        }
    };
    //----------------------------------------------------------------------------------------------
    @Override
    public void stopTalking() {
        //stops TTS and plays random song on return from dialog
         mySpeaker.stop();
         thePlayer.start();
    }
    //----------------------------------------------------------------------------------------------
    ActivityResultLauncher<Intent> editArl = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    //start random music on return
                    thePlayer.start();
                    if (result.getResultCode() == Activity.RESULT_OK) {

                        Intent resultIntent = result.getData();
                        //get new description from return intent
                        String newDescription = resultIntent.getStringExtra(getResources().getString(R.string.newDescriptionKey));
                        //get image id from return intent
                        int imageId = resultIntent.getIntExtra(getResources().getString(R.string.imageIdKey),0);
                        //get pictures data from DB
                        ContentValues itemData = imageDB.getDataByImageId(imageId);
                        //update description
                        itemData.put("description",newDescription);
                        imageDB.updateData((int)itemData.get("_id"),itemData);
                        //update list to show new description
                        fillList();

                    } else {
                        Toast.makeText(MainActivity.this,"No description added!",Toast.LENGTH_SHORT).show();
                    }
                }
            });
    //----------------------------------------------------------------------------------------------
    @Override
    public void onCompletion(MediaPlayer mp) {
        thePlayer.start();
    }
    //----------------------------------------------------------------------------------------------
    public void playAudioAtCursor(){

        String audioFileName;
        int audioDataIndex;
        int randomAudio;

        try{
            //get random number in audio cursor length
            randomAudio = (int)(audioCursor.getCount() * Math.random() + 1);
            audioDataIndex = audioCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
            //move cursor to random position
            audioCursor.move(randomAudio);
            //prepare and player the random audio
            audioFileName = audioCursor.getString(audioDataIndex);
            thePlayer.setDataSource(audioFileName);
            thePlayer.prepare();
            thePlayer.start();
        }catch (IOException e) {

        }
    }
    //----------------------------------------------------------------------------------------------
    public void assignObjects(){

        //assign queries
        String[]imageQueryFields = {MediaStore.Images.Media._ID,MediaStore.Images.Media.DATA};
        String[]audioQueryFields = {MediaStore.Audio.Media._ID,MediaStore.Audio.Media.DATA};
        //assign DB, TTS, and MediaPlayer
        imageDB = new DataSQLiteDataBase(this);
        mySpeaker = new TextToSpeech(this,this);
        thePlayer = new MediaPlayer();
        //query audio and image Media
        imageCursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageQueryFields,null,null,MediaStore.Images.Media.DEFAULT_SORT_ORDER);
        audioCursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, audioQueryFields,null,null,MediaStore.Audio.Media.TITLE + " ASC");
        //prepare the media player
        thePlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        thePlayer.setOnCompletionListener(this);


    }
}
//==================================================================================================