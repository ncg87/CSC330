package com.example.texttospeech;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

//=================================================================================================
public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {
    //-------------------------------------------------------------------------------------------------
    private TextToSpeech mySpeaker;
    //-------------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Intent ttsIntent;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//----Insane Android
        findViewById(R.id.speak).setClickable(false);

        mySpeaker = new TextToSpeech(this,this);
    }
    //-------------------------------------------------------------------------------------------------
    public void onInit(int status) {

        if (status == TextToSpeech.SUCCESS) {
            Toast.makeText(this,R.string.talk_prompt,Toast.LENGTH_SHORT).show();
            mySpeaker.setOnUtteranceProgressListener(myListener);
            findViewById(R.id.speak).setClickable(true);
        } else {
            Toast.makeText(this,"TTS could not be initialized",Toast.LENGTH_LONG).show();
            finish();
        }
    }
    //-------------------------------------------------------------------------------------------------
    public void myClickListener(View view) {

        String whatToSay;

        switch (view.getId()) {
            case R.id.speak:
                whatToSay = ((EditText)findViewById(R.id.what_to_say)).getText().toString();
                if (whatToSay.length() == 0) {
                    whatToSay = getResources().getString(R.string.nothing_to_say);
                }
                if (mySpeaker.speak(whatToSay,TextToSpeech.QUEUE_ADD,null,"WHAT_I_SAID") ==
                        TextToSpeech.SUCCESS) {
                    findViewById(R.id.speak).setClickable(false);
                } else {
                    Toast.makeText(this,"Failed to speak",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.exit:
                finish();
                break;
            default:
                break;
        }
    }
    //-------------------------------------------------------------------------------------------------
    private UtteranceProgressListener myListener = new UtteranceProgressListener() {
        @Override
        public void onStart(String utteranceId) {
        }

        @Override
        public void onDone(String utteranceId) {

            myHandler.post(myUtteranceCompleted);
            //if(myHandler.postDelayed(myUtteranceCompleted,))
        }

        @Override
        public void onError(String utteranceId) {
        }
    };
    //-------------------------------------------------------------------------------------------------
    private Handler myHandler = new Handler();

    private final Runnable myUtteranceCompleted = new Runnable() {

        public void run() {

            ((EditText)findViewById(R.id.what_to_say)).setText("");
            findViewById(R.id.speak).setClickable(true);
        }
    };
    //-------------------------------------------------------------------------------------------------
    @Override
    public void onDestroy() {

        super.onDestroy();
        myHandler.removeCallbacks(myUtteranceCompleted);
        mySpeaker.shutdown();
    }
//-------------------------------------------------------------------------------------------------
}
//=================================================================================================