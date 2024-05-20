package com.example.datacontactsemail;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Map;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

//=================================================================================================
public class MainActivity extends AppCompatActivity {
    //-------------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPermissions.launch(new String[] {Manifest.permission.READ_CONTACTS});
    }
    //-------------------------------------------------------------------------------------------------
    private void goOnCreating(boolean havePermission) {

        if (havePermission) {
            setContentView(R.layout.activity_main);
        } else {
            Toast.makeText(this,"Need permission",Toast.LENGTH_LONG).show();
            finish();}
    }
    //-------------------------------------------------------------------------------------------------
    public void myClickHandler(View view) {

        String nameToSearchFor;
        String[] emailToSendTo = new String[1];
        Intent emailIntent;
        String[] searchResult;

        switch (view.getId()) {
            case R.id.search:
                ((TextView)findViewById(R.id.email_address)).setText(null);
                nameToSearchFor = ((EditText)findViewById(R.id.search_for)).getText().toString();
                if (nameToSearchFor != null && !nameToSearchFor.isEmpty()) {
                    searchResult = searchForEmailAddressByName(nameToSearchFor);
                    setEmailAddress(searchResult[0],searchResult[1]);
                } else {
                    startContacts.launch(null);
                }
                break;
            case R.id.email:
                emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setType("plain/text");
                emailToSendTo[0] =
                        ((EditText)findViewById(R.id.email_address)).getText().toString();
                if (emailToSendTo[0] != null && !emailToSendTo[0].isEmpty()) {
                    //emailIntent.putExtra(Intent.EXTRA_EMAIL,emailToSendTo);
                }
                startActivity(emailIntent);
                break;
            default:
                break;
        }
    }
    //-------------------------------------------------------------------------------------------------
    private String[] searchForEmailAddressByName(String nameToSearchFor) {

        final String[] projection = new String[] {
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Email.DATA
        };
        Cursor contactsCursor;
        String contactName = null;
        int nameIndex;
        boolean contactFound;
        String emailAddress;

        contactsCursor = getContentResolver().query(
                ContactsContract.CommonDataKinds.Email.CONTENT_URI,projection,null,null,null);
        contactFound = false;
        if (contactsCursor.moveToFirst()) {
            nameIndex = contactsCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
            do {
                contactName = contactsCursor.getString(nameIndex);
                if (contactName.toUpperCase().contains(nameToSearchFor.toUpperCase())) {
                    contactFound = true;
                }
            } while (!contactFound && contactsCursor.moveToNext());
        }

        if (contactFound) {
            emailAddress = contactsCursor.getString(contactsCursor.getColumnIndexOrThrow(
                    ContactsContract.CommonDataKinds.Email.DATA));
        } else {
            contactName = nameToSearchFor;
            emailAddress = null;
        }
        contactsCursor.close();
        return(new String[] {contactName,emailAddress});
    }
    //-------------------------------------------------------------------------------------------------
    ActivityResultLauncher<Void> startContacts = registerForActivityResult(
            new ActivityResultContracts.PickContact(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri contactData) {

                    Cursor contactsCursor;
                    int contactId;
                    String contactName;

                    if (contactData != null) {
                        contactsCursor = getContentResolver().query(contactData,null,null,null,null);
                        if (contactsCursor.moveToFirst()) {
                            contactId = contactsCursor.getInt(contactsCursor.getColumnIndexOrThrow(
                                    ContactsContract.Contacts._ID));
                            contactName = contactsCursor.getString(contactsCursor.getColumnIndexOrThrow(
                                    ContactsContract.Contacts.DISPLAY_NAME));
                            ((TextView)findViewById(R.id.search_for)).setText(contactName);
                            setEmailAddress(contactName,searchForEmailAddressById(contactId));
                        }
                        contactsCursor.close();
                    } else {
                        Toast.makeText(MainActivity.this,"No contact selected",Toast.LENGTH_LONG).show();
                    }
                }
            });
    //-------------------------------------------------------------------------------------------------
    private String searchForEmailAddressById(int contactId) {

        String[] projection = new String[] {
                ContactsContract.CommonDataKinds.Email.CONTACT_ID,
                ContactsContract.CommonDataKinds.Email.DATA
        };
        Cursor emailCursor;
        String emailAddress;

        emailCursor = getContentResolver().query(
                ContactsContract.CommonDataKinds.Email.CONTENT_URI,projection,"CONTACT_ID = ?",
                new String[]{Integer.toString(contactId)},null);
        if (emailCursor.moveToFirst()) {
            emailAddress = emailCursor.getString(emailCursor.getColumnIndexOrThrow(
                    ContactsContract.CommonDataKinds.Email.DATA));
        } else {
            emailAddress = null;
        }
        emailCursor.close();
        return(emailAddress);
    }
    //-------------------------------------------------------------------------------------------------
    private void setEmailAddress(String name,String emailAddress) {

        EditText nameField;
        EditText emailAddressField;

        nameField = findViewById(R.id.search_for);
        emailAddressField = findViewById(R.id.email_address);
        if (emailAddress != null) {
            nameField.setText(name);
            emailAddressField.setText(emailAddress);
        } else {
            emailAddressField.setHint("\""+name+"\"" +" not found");
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
}
//=================================================================================================