package com.example.timedtext;

import android.Manifest;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

//==================================================================================================
public class MainActivity extends AppCompatActivity {
    //----------------------------------------------------------------------------------------------
    private static DataSQLiteDB savedNotesDB;
    //----------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPermissions.launch(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE});
    }
    //----------------------------------------------------------------------------------------------
    public void goOnCreating(boolean havePermission){
        if(havePermission){
            setContentView(R.layout.activity_main);
            savedNotesDB = new DataSQLiteDB(this);
        }
    }
    //----------------------------------------------------------------------------------------------
    public void myClickHandler(View view){

        EditText theNote = findViewById(R.id.edit_text);
        Intent nextActivity;
        String note;

        note = theNote.getText().toString();
        switch(view.getId()){
            case R.id.notes_button:
                nextActivity = new Intent(this, SavedNotes.class);
                startActivity(nextActivity);
                break;
            case R.id.save_button:
                addNote(note);
                theNote.setText("");
                break;
            default:
                break;
        }
    }
    //----------------------------------------------------------------------------------------------
    public void addNote(String note){

        ContentValues noteData;
        long unixTime = System.currentTimeMillis();
        Date itemDate = new Date(unixTime);
        String noteDate = itemDate.toString();

        noteData = new ContentValues();
        noteData.put("note_text",note);
        noteData.put("note_save_time", noteDate);
        if(!savedNotesDB.addNote(noteData)){
            Toast.makeText(this, "Error Adding " + note + " to DB!", Toast.LENGTH_LONG).show();
        }

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
        savedNotesDB.close();
    }
    //----------------------------------------------------------------------------------------------
}
//==================================================================================================