package com.example.countmein;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    public static String PREFERENCES_NAME = "TimeOpened";
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences currentPreferences;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        currentPreferences = getSharedPreferences(PREFERENCES_NAME,MODE_PRIVATE);
        long timesRun;
        TextView theText;

        theText = findViewById(R.id.timesRan);
        timesRun = currentPreferences.getLong(getResources().getString(R.string.timesrun_key),1);
        theText.setText("" + (int) timesRun);

        SharedPreferences.Editor editor;
        editor = currentPreferences.edit();
        editor.putLong(getResources().getString(R.string.timesrun_key),++timesRun);
        editor.commit();

    }
}