package com.example.sqlitedatabaseexample;

import android.Manifest;
import android.content.ContentValues;
import android.database.Cursor;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Map;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

//=================================================================================================
public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    //-------------------------------------------------------------------------------------------------
    private DataSQLiteDB ratedMusicDB;
    private Cursor audioMediaCursor;
    private MediaPlayer myPlayer;
    //-------------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getPermissions.launch(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE});
    }
    //-------------------------------------------------------------------------------------------------
    private void goOnCreating(boolean havePermission) {

        AudioAttributes.Builder audioAttributes;
        String[] queryFields = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA
        };

        if (havePermission) {
            setContentView(R.layout.activity_main);
            ratedMusicDB = new DataSQLiteDB(this);
            audioMediaCursor = getContentResolver().query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,queryFields,null,null,
                    MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
            if (audioMediaCursor != null & audioMediaCursor.getCount() > 0) {
                updateMusicDBFromContent();
                fillList();
                audioAttributes = new AudioAttributes.Builder();
                audioAttributes.setLegacyStreamType(AudioManager.STREAM_MUSIC);
                myPlayer = new MediaPlayer();
                myPlayer.setAudioAttributes(audioAttributes.build());
            } else {
                Toast.makeText(this,"Cannot query MediaStore for audio",Toast.LENGTH_LONG).show();
                finish();
            }
        } else {
            Toast.makeText(this,"Need permission",Toast.LENGTH_LONG).show();
            finish();
        }
    }
    //-------------------------------------------------------------------------------------------------
    private void updateMusicDBFromContent() {

        ContentValues songData;
        int audioMediaId;

        audioMediaCursor.moveToFirst();
        do {
            audioMediaId = audioMediaCursor.getInt(
                    audioMediaCursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
//----If the song is not in the DB, add it
            if (ratedMusicDB.getSongByAudioMediaId(audioMediaId) == null) {
                songData = new ContentValues();
                songData.put("audio_media_id",audioMediaId);
                songData.put("song_name",audioMediaCursor.getString(
                        audioMediaCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)));
                songData.put("data_file",audioMediaCursor.getString(
                        audioMediaCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
                songData.put("plays",0);
                if (!ratedMusicDB.addSong(songData)) {
                    Toast.makeText(this,"Error adding \"" + audioMediaCursor.getString(
                                    audioMediaCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)) + "\" to DB",
                            Toast.LENGTH_LONG).show();
                }
            }
        } while (audioMediaCursor.moveToNext());
    }
    //-------------------------------------------------------------------------------------------------
    private void fillList() {

        String[] displayFields = {"song_name","plays"};
        int[] displayViews = {R.id.song_name,R.id.plays};

        ListView theList;
        SimpleCursorAdapter listAdapter;

        theList = findViewById(R.id.the_list);
        listAdapter = new SimpleCursorAdapter(this,R.layout.list_item,
                ratedMusicDB.fetchAllSongs(),displayFields,displayViews,0);
        theList.setAdapter(listAdapter);
        theList.setOnItemClickListener(this);
    }
    //-------------------------------------------------------------------------------------------------
    public void onItemClick(AdapterView<?> parent,View view,int position,long rowId) {

        ContentValues songData;
        int playsSoFar;
        TextView playsView;

        myPlayer.reset();
        songData = ratedMusicDB.getSongById(rowId);
        try {
            myPlayer.setDataSource(songData.getAsString("data_file"));
            myPlayer.prepare();
            myPlayer.start();
        } catch (IOException e) {
            Toast.makeText(this,"Error playing \"" + songData.getAsString("song_name") + "\"",
                    Toast.LENGTH_LONG).show();
            return;
        }
        playsSoFar = songData.getAsInteger("plays");
        playsSoFar++;
        songData.put("plays",playsSoFar);
        ratedMusicDB.updateSong(songData.getAsInteger("_id"),songData);
//----Could be crude and requery
        playsView = (TextView)view.findViewById(R.id.plays);
        playsView.setText(String.format("%d",playsSoFar));
    }

    //----The above assumes user hasn't deleted media. A more robust solution would search through the
//----DB using the cursor, like this ...
//        songData = ratedMusicDB.getSongById(rowId);
////----Supposedly getItem will get a Cursor with the data, but no success
//        audioMediaId = songData.getAsInteger("audio_media_id");
//        songName = songData.getAsString("song_name");
//
//        songFound = false;
//        if (audioMediaCursor.moveToFirst()) {
//            idIndex = audioMediaCursor.getColumnIndex(MediaStore.Audio.Media._ID);
//            do {
//                songFound = audioMediaId == audioMediaCursor.getInt(idIndex);
//            } while (!songFound && audioMediaCursor.moveToNext());
//        }
//
//        if (songFound) {
//        } else {
////----Delete any song that can't be found
//            ratedMusicDB.deleteSong(songData.getAsInteger("_id"));
//            Toast.makeText(this,"\""+songName+"\" has been deleted",Toast.LENGTH_LONG).show();
//            cursorAdapter.changeCursor(ratedMusicDB.fetchAllSongs());
//            return;
//        }
//-------------------------------------------------------------------------------------------------
    public void myClickHandler(View view) {

        switch (view.getId()) {
            case R.id.header:
                myPlayer.reset();
                break;
            default:
                break;
        }
    }
    //-------------------------------------------------------------------------------------------------
    @Override
    public void onDestroy() {

        super.onDestroy();
        audioMediaCursor.close();
        myPlayer.release();
        ratedMusicDB.close();
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
}
//=================================================================================================