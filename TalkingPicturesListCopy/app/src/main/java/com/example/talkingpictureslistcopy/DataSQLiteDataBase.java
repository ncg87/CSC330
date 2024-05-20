package com.example.talkingpictureslistcopy;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
//==================================================================================================
public class DataSQLiteDataBase {
    //----------------------------------------------------------------------------------------------
    public static final String DATABASE_NAME = "SavedNotes.db";
    private static final int DATABASE_VERSION = 17;


    private static final String IMAGE_NOTE_TABLE_NAME = "SavedNotes";
    public static final String CREATE_IMAGE_NOTE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + IMAGE_NOTE_TABLE_NAME +
                    "(_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "imageId INTEGER UNIQUE NOT NULL," +
                    "description TEXT," +
                    "recording BLOB"+
                    ");";
    private SQLiteDatabase theDB;
    private DatabaseHelper dbHelper;
    //----------------------------------------------------------------------------------------------
    public DataSQLiteDataBase(Context theContext){
        dbHelper = new DatabaseHelper(theContext);
        theDB = dbHelper.getWritableDatabase();
    }
    //----------------------------------------------------------------------------------------------
    public void close(){

        dbHelper.close();
        theDB.close();
    }
    //----------------------------------------------------------------------------------------------
    public boolean addData(ContentValues noteData){
        return(theDB.insert(IMAGE_NOTE_TABLE_NAME,null,noteData) >= 0);
    }
    //----------------------------------------------------------------------------------------------
    public boolean updateData(int dataID, ContentValues data){
        return(theDB.update(IMAGE_NOTE_TABLE_NAME,data,"_id =" + dataID, null) > 0);
    }
    //----------------------------------------------------------------------------------------------
    public boolean deleteData(long dataID){
        return(theDB.delete(IMAGE_NOTE_TABLE_NAME,"_id =" + dataID, null) > 0);
    }
    //----------------------------------------------------------------------------------------------
    public Cursor fetchAllData(){

        String[] fieldNames = {"_id","imageId","description","recording"};

        return(theDB.query(IMAGE_NOTE_TABLE_NAME,fieldNames,null,null,null,null,"_id"));
    }
    //----------------------------------------------------------------------------------------------
    public ContentValues getDataById(long dataId){
        Cursor cursor;
        ContentValues data;

        cursor = theDB.query(IMAGE_NOTE_TABLE_NAME,null,"_id = \"" + dataId + "\"", null, null, null, null);

        data = noteDataFromCursor(cursor);
        cursor.close();
        return data;
    }
    //----------------------------------------------------------------------------------------------
    public ContentValues getDataByImageId(long imageMediaId){

        Cursor cursor;
        ContentValues imageData;

        cursor = theDB.query(IMAGE_NOTE_TABLE_NAME,null,"imageId = " + imageMediaId,
                null,null,null,null);
        imageData = noteDataFromCursor(cursor);
        cursor.close();
        return (imageData);

    }
    //----------------------------------------------------------------------------------------------
    public ContentValues noteDataFromCursor(Cursor cursor){
        String[] fieldNames;
        int index;
        ContentValues data;

        if(cursor != null && cursor.moveToFirst()){
            fieldNames = cursor.getColumnNames();
            data = new ContentValues();
            for(index = 0; index < fieldNames.length; index++){
                if(fieldNames[index].equals("_id")){
                    data.put("_id",cursor.getInt(index));
                } else if (fieldNames[index].equals("imageId")) {
                    data.put("imageId",cursor.getInt(index));
                } else if (fieldNames[index].equals("description")) {
                    data.put("description",cursor.getString(index));
                } else if (fieldNames[index].equals("recording")) {
                    data.put("recording",cursor.getBlob(index));
                }
            }
            return data;
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

            db.execSQL(CREATE_IMAGE_NOTE_TABLE);
        }
        //------------------------------------------------------------------------------------------
        @Override
        public void onOpen(SQLiteDatabase db) {

            super.onOpen(db);
        }
        //------------------------------------------------------------------------------------------
        public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion) {

            db.execSQL("DROP TABLE IF EXISTS " + IMAGE_NOTE_TABLE_NAME);
            onCreate(db);
        }
        //----------------------------------------------------------------------------------------------
    }
//==================================================================================================
}
//==================================================================================================

