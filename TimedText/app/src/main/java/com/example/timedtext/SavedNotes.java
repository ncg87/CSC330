package com.example.timedtext;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

//==================================================================================================
public class SavedNotes extends AppCompatActivity {
    //----------------------------------------------------------------------------------------------
    private static DataSQLiteDB savedNotesDB;
    //----------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_notes);
        savedNotesDB = new DataSQLiteDB(this);
        fillList();
    }
    //----------------------------------------------------------------------------------------------
    private void fillList(){

        String[] displayFields = {"note_text","note_save_time"};
        int[] displayViews = {R.id.note,R.id.note_time};

        ListView theList;
        SimpleCursorAdapter listAdapter;

        theList = findViewById(R.id.list_notes);
        listAdapter = new SimpleCursorAdapter(this,R.layout.list_cell,savedNotesDB.fetchAllNotes(),displayFields,displayViews,0);
        theList.setAdapter(listAdapter);
    }
    //----------------------------------------------------------------------------------------------
    public void onDestroy(){

        super.onDestroy();
        savedNotesDB.close();
    }
    //----------------------------------------------------------------------------------------------
}
//==================================================================================================