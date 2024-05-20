package com.example.talkingpictureslistcopy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.view.View;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;

//==================================================================================================
public class EditUI extends AppCompatActivity {
    //----------------------------------------------------------------------------------------------
    private int imageId;
    private MediaRecorder mediaRecorder;
    private boolean isRecording = false;
    private boolean isStopped = true;
    private boolean alreadyRecorded = false;
    private String recordFileName;
    //----------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_ui);
        //set image view picture
        String imageUri = this.getIntent().getStringExtra(getResources().getString(R.string.imageUriKey));
        imageId = this.getIntent().getIntExtra(getResources().getString(R.string.imageIdKey),0);
        ImageView theImage = findViewById(R.id.theEditImage);
        theImage.setImageURI(Uri.parse(imageUri));

        //initialize media recorder
        initializeMediaRecorder();
        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            Log.i("AUDIO ERROR","PREPARING RECORDER");
            Toast.makeText(this, "RECORDING FAILED", Toast.LENGTH_SHORT).show();
        }
    }
    //----------------------------------------------------------------------------------------------
    public void myClickHandler(View view) throws IOException {

        //gets decription of edit text
        String newDesciption = ((EditText) findViewById(R.id.theEdit)).getText().toString();

        switch(view.getId()){
            case R.id.saveButton:
                //saves new description
                Intent returnIntent = new Intent();
                if(!newDesciption.equals("") && newDesciption != null){
                    //get description and send it back to main activity
                    returnIntent.putExtra(getResources().getString(R.string.newDescriptionKey),newDesciption);
                    returnIntent.putExtra(getResources().getString(R.string.imageIdKey),imageId);
                    byte[] data = audioToByteArray();
                    if(alreadyRecorded){
                        returnIntent.putExtra(getResources().getString(R.string.audioFileKey),data);
                    }else{
                        returnIntent.putExtra(getResources().getString(R.string.audioFileKey), (String) null);
                    }
                    setResult(RESULT_OK,returnIntent);
                }else{
                    setResult(RESULT_CANCELED);
                }
                finish();
                break;
            case R.id.recordButton:
                //starts recording and takes care of errors with booleans
                if(isStopped) {
                    if(alreadyRecorded){
                        Toast.makeText(this, "Already recorded", Toast.LENGTH_SHORT).show();
                    }else {
                        mediaRecorder.start();
                        Toast.makeText(this, "Starting recording", Toast.LENGTH_SHORT).show();
                        isStopped = false;
                        isRecording = true;
                    }
                }else{
                    Toast.makeText(this, "Already Recording", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.stopButton:
                //stops recording and takes care of errors with booleans
                if(isRecording) {
                    Toast.makeText(this, "Stopping recording", Toast.LENGTH_SHORT).show();
                    mediaRecorder.stop();
                    isStopped = true;
                    isRecording = false;
                    alreadyRecorded = true;
                }else{
                    Toast.makeText(this, "Already Stopped", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }
    //----------------------------------------------------------------------------------------------
    public byte[] audioToByteArray() throws IOException {
        //creates a byte array output stream to convert audiofile to byte array and input stream to read it
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        InputStream inputStream = Files.newInputStream(new File(recordFileName).toPath());
        int index;
        byte[] data = new byte[8192];
        //writes file to byte array
        while ((index = inputStream.read(data, 0, data.length)) != -1) {
            byteArrayOutputStream.write(data, 0, index);
        }
        //closes input stream and output stream
        try {
            inputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        byteArrayOutputStream.close();
        //returns byte array
        return byteArrayOutputStream.toByteArray();
    }
    //----------------------------------------------------------------------------------------------
    public void initializeMediaRecorder(){

        //prepares media recorder and sets output file
        recordFileName = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).toString() + "/recording" + imageId;

        mediaRecorder = new android.media.MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setOutputFile(recordFileName);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
//==================================================================================================