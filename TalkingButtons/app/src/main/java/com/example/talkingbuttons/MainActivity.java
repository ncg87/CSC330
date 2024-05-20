package com.example.talkingbuttons;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
//==================================================================================================
public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {
    //----------------------------------------------------------------------------------------------
    private TextToSpeech mySpeaker;
    private int fiveSeconds;
    //----------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mySpeaker = new TextToSpeech(this,this);
        fiveSeconds = getResources().getInteger(R.integer.speak_time);
        myHandler.postDelayed(myUtteranceCompleted,fiveSeconds);
        myUtteranceCompleted.run();
    }
    //----------------------------------------------------------------------------------------------
    public void myClickHandler(View view){

        String whatToSay = " ";

        switch (view.getId()){
            case R.id.a1:
                whatToSay = getString(R.string.a1);
                break;
            case R.id.a2:
                whatToSay = getString(R.string.a2);
                break;
            case R.id.a3:
                whatToSay = getString(R.string.a3);
                break;
            case R.id.a4:
                whatToSay = getString(R.string.a4);
                break;
            case R.id.b1:
                whatToSay = getString(R.string.b1);
                break;
            case R.id.b2:
                whatToSay = getString(R.string.b2);
                break;
            case R.id.b3:
                whatToSay = getString(R.string.b3);
                break;
            case R.id.exit_button:
                finish();
                break;
        }
        if (mySpeaker.speak(whatToSay,TextToSpeech.QUEUE_ADD,null,"WHAT_I_SAID") ==
                TextToSpeech.SUCCESS) {

        }
    }
    //----------------------------------------------------------------------------------------------
    private Handler myHandler = new Handler();

    private final Runnable myUtteranceCompleted = new Runnable() {
        @Override
        public void run() {
            if (!mySpeaker.isSpeaking()){
                myHandler.postDelayed(myUtteranceCompleted,fiveSeconds);
                if (mySpeaker.speak(getString(R.string.nothing_to_say), TextToSpeech.QUEUE_ADD, null, "WHAT_I_SAID") ==
                        TextToSpeech.SUCCESS) {
                    //Toast.makeText(context, getString(R.string.nothing_to_say), Toast.LENGTH_SHORT).show();
                }
            }
        }
    };
    //----------------------------------------------------------------------------------------------
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            //Toast.makeText(this,R.string.talk_prompt,Toast.LENGTH_SHORT).show();
            mySpeaker.setOnUtteranceProgressListener(myListener);
        } else {
            Toast.makeText(this,"TTS could not be initialized",Toast.LENGTH_LONG).show();
            finish();
        }
    }
    //----------------------------------------------------------------------------------------------
    private UtteranceProgressListener myListener = new UtteranceProgressListener() {
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
    public void onDestroy(){

        super.onDestroy();
        myHandler.removeCallbacks(myUtteranceCompleted);
        mySpeaker.shutdown();
    }
    //----------------------------------------------------------------------------------------------
}
//================================================================================================