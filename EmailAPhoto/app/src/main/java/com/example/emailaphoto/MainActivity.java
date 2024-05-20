package com.example.emailaphoto;

import android.Manifest;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.Toast;

import java.util.Map;
//==================================================================================================
public class MainActivity extends AppCompatActivity {
    //----------------------------------------------------------------------------------------------
    Intent emailIntent;
    //----------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPermissions.launch(new String[] {Manifest.permission.READ_CONTACTS});

    }
    //----------------------------------------------------------------------------------------------
    private void goOnCreating(boolean havePermission) {
        if(havePermission){
            setContentView(R.layout.activity_main);
            startGallery.launch("image/*");
        }else{
            Toast.makeText(this,"Need permission",Toast.LENGTH_LONG).show();
            finish();
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
    ActivityResultLauncher<Void> startContacts = registerForActivityResult(
            new ActivityResultContracts.PickContact(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri contactData) {

                    Cursor contactsCursor;
                    int contactId;
                    String contactName;
                    String [] emailAddress;

                    if (contactData != null) {
                        contactsCursor = getContentResolver().query(contactData,null,null,null,null);
                        if (contactsCursor.moveToFirst()) {
                            contactId = contactsCursor.getInt(contactsCursor.getColumnIndexOrThrow(
                                    ContactsContract.Contacts._ID));
                            contactName = contactsCursor.getString(contactsCursor.getColumnIndexOrThrow(
                                    ContactsContract.Contacts.DISPLAY_NAME));
                            emailAddress = new String[] {searchForEmailAddressById(contactId)};

                            emailIntent.setType("plain/text");
                            emailIntent.putExtra(Intent.EXTRA_EMAIL,emailAddress);
                            emailIntent.putExtra(Intent.EXTRA_SUBJECT,"Hello, " + contactName);
                            emailIntent.putExtra(Intent.EXTRA_TEXT,"Hello " + contactName + ",");
                            startActivity(emailIntent);
                        }
                        contactsCursor.close();
                    } else {
                        Toast.makeText(MainActivity.this,"No contact selected",Toast.LENGTH_LONG).show();
                        finish();
                    }
                }
            });
    //----------------------------------------------------------------------------------------------
    private String searchForEmailAddressById(int contactId){

        String[] projection = new String[] {
                ContactsContract.CommonDataKinds.Email.CONTACT_ID,
                ContactsContract.CommonDataKinds.Email.DATA};
        Cursor emailCursor;
        String emailAddress;

        emailCursor = getContentResolver().query(
                ContactsContract.CommonDataKinds.Email.CONTENT_URI,projection,"CONTACT_ID = ?",
                new String[]{Integer.toString(contactId)},null);
        if(emailCursor.moveToFirst()){
            emailAddress = emailCursor.getString(emailCursor.getColumnIndexOrThrow(
                    ContactsContract.CommonDataKinds.Email.DATA));
        } else {
            emailAddress = null;
        }
        emailCursor.close();
        return(emailAddress);
    }
    //----------------------------------------------------------------------------------------------
    ActivityResultLauncher<String> startGallery = registerForActivityResult(
            new ActivityResultContracts.GetContent(),new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri resultUri) {

                    if (resultUri != null) {
                        emailIntent = new Intent(Intent.ACTION_SEND);
                        emailIntent.putExtra(Intent.EXTRA_STREAM,resultUri);
                        startContacts.launch(null);
                    } else {
                        Toast.makeText(MainActivity.this,"No picture chosen!!!",Toast.LENGTH_LONG).show();
                        finish();
                    }
                }
            });
    //----------------------------------------------------------------------------------------------
}
//==================================================================================================