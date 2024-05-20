package com.example.timedtext;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
//==================================================================================================
public class DataSQLiteDB {
    //----------------------------------------------------------------------------------------------
    public static final String DATABASE_NAME = "SavedNotes.db";
    private static final int DATABASE_VERSION = 2;


    private static final String SAVED_NOTES_TABLE_NAME = "SavedNotes";
    public static final String CREATE_SAVED_NOTES_TABLE =
            "CREATE TABLE IF NOT EXISTS " + SAVED_NOTES_TABLE_NAME +
                    "(_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "note_text TEXT," +
                    "note_save_time TEXT" +
                    ");";
    private SQLiteDatabase theDB;
    private DatabaseHelper dbHelper;
    //----------------------------------------------------------------------------------------------
    public DataSQLiteDB(Context theContext){
        dbHelper = new DatabaseHelper(theContext);
        theDB = dbHelper.getWritableDatabase();
    }
    //----------------------------------------------------------------------------------------------
    public void close(){

        dbHelper.close();
        theDB.close();
    }
    //----------------------------------------------------------------------------------------------
    public boolean addNote(ContentValues noteData){
        return(theDB.insert(SAVED_NOTES_TABLE_NAME,null,noteData) >= 0);
    }
    //----------------------------------------------------------------------------------------------
    public boolean updateNote(long noteID, ContentValues noteData){
        return(theDB.update(SAVED_NOTES_TABLE_NAME,noteData,"_id =" + noteID, null) > 0);
    }
    //----------------------------------------------------------------------------------------------
    public boolean deleteNote(long noteID){
        return(theDB.delete(SAVED_NOTES_TABLE_NAME,"_id =" + noteID, null) > 0);
    }
    //----------------------------------------------------------------------------------------------
    public Cursor fetchAllNotes(){

        String[] fieldNames = {"_id","note_text","note_save_time"};

        return(theDB.query(SAVED_NOTES_TABLE_NAME,fieldNames,null,null,null,null,"_id"));
    }
    //----------------------------------------------------------------------------------------------
    public ContentValues getNoteById(long noteId){
        Cursor cursor;
        ContentValues noteData;

        cursor = theDB.query(SAVED_NOTES_TABLE_NAME,null,"_id = \"" + noteId + "\"", null, null, null, null);

        noteData = noteDataFromCursor(cursor);
        cursor.close();
        return noteData;
    }
    //----------------------------------------------------------------------------------------------
    public ContentValues noteDataFromCursor(Cursor cursor){
        String[] fieldNames;
        int index;
        ContentValues noteData;

        if(cursor != null && cursor.moveToFirst()){
            fieldNames = cursor.getColumnNames();
            noteData = new ContentValues();
            for(index = 0; index < fieldNames.length; index++){
                if(fieldNames[index].equals("_id")){
                    noteData.put("_id",cursor.getInt(index));
                } else if (fieldNames[index].equals("note_text")) {
                    noteData.put("note_text",cursor.getString(index));
                } else if (fieldNames[index].equals("note_save_time")) {
                    noteData.put("note_save_time",cursor.getString(index));
                }
            }
            return noteData;
        }else{
            return null;
        }
    }
    //----------------------------------------------------------------------------------------------
//==================================================================================================
    private static class DatabaseHelper extends SQLiteOpenHelper {
        //------------------------------------------------------------------------------------------
        public DatabaseHelper(Context context) {

            super(context,DATABASE_NAME,null,DATABASE_VERSION);
        }
        //------------------------------------------------------------------------------------------
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(CREATE_SAVED_NOTES_TABLE);
        }
        //------------------------------------------------------------------------------------------
        @Override
        public void onOpen(SQLiteDatabase db) {

            super.onOpen(db);
        }
        //------------------------------------------------------------------------------------------
        public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion) {

            db.execSQL("DROP TABLE IF EXISTS " + SAVED_NOTES_TABLE_NAME);
            onCreate(db);
        }
    //----------------------------------------------------------------------------------------------
    }
//==================================================================================================
}
//==================================================================================================

