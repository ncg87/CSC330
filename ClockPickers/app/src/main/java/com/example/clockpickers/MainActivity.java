package com.example.clockpickers;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.util.Date;
import java.util.GregorianCalendar;

//====================================================
public class MainActivity extends AppCompatActivity {
    private GregorianCalendar calender;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private int oneMinute;
    //-------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        oneMinute = getResources().getInteger(R.integer.one_minute);
        datePicker = findViewById(R.id.date_picker);
        timePicker = findViewById(R.id.timePicker);
        calender = new GregorianCalendar(datePicker.getYear(),datePicker.getMonth(),datePicker.getDayOfMonth(),timePicker.getHour(),timePicker.getMinute());
        myProgressor.run();
    }
    //--------------------------------------------------
    private Handler myHandler = new Handler();
    private final Runnable myProgressor = new Runnable() {
        @Override
        public void run() {
            if(myHandler.postDelayed(myProgressor,oneMinute)){
                calender.set(datePicker.getYear(),datePicker.getMonth(),datePicker.getDayOfMonth(),timePicker.getHour(),timePicker.getMinute());
                calender.add(GregorianCalendar.SECOND, 60);
                datePicker.updateDate(calender.getTime().getYear(),calender.getTime().getMonth(),calender.getTime().getDay());
                timePicker.setHour(calender.getTime().getHours());
                timePicker.setMinute(calender.getTime().getMinutes());
            }
        }
    };
    //------------------------------------------------
    protected void onDestroy(){
        super.onDestroy();
        myHandler.removeCallbacks(myProgressor);
    }
    //------------------------------------------------
}