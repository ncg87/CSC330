package com.example.dateandtimepickers;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import java.util.GregorianCalendar;
//=================================================================================================
public class MainActivity extends AppCompatActivity
        implements DatePicker.OnDateChangedListener,TimePicker.OnTimeChangedListener {
    //-------------------------------------------------------------------------------------------------
    private GregorianCalendar calendar;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private TextView secondsText;
    //-------------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        datePicker = findViewById(R.id.date_picker);
        datePicker.init(datePicker.getYear(),datePicker.getMonth(),datePicker.getDayOfMonth(),this);
        timePicker = findViewById(R.id.time_picker);
        timePicker.setOnTimeChangedListener(this);
        calendar = new GregorianCalendar(datePicker.getYear(),datePicker.getMonth(),
                datePicker.getDayOfMonth(),timePicker.getHour(),timePicker.getMinute());
        secondsText = findViewById(R.id.unix_minutes);
        setUNIXSeconds();
    }
    //-------------------------------------------------------------------------------------------------
    public void onDateChanged(DatePicker view,int year,int monthOfYear,int dayOfMonth) {

        setUNIXSeconds();
    }
    //-------------------------------------------------------------------------------------------------
    public void onTimeChanged(TimePicker view,int hourOfDay,int minute) {

        setUNIXSeconds();
    }
    //-------------------------------------------------------------------------------------------------
    private void setUNIXSeconds() {

        calendar.set(datePicker.getYear(),datePicker.getMonth(),datePicker.getDayOfMonth(),
                timePicker.getHour(),timePicker.getMinute());
        secondsText.setText(String.format("%d seconds of UNIX",calendar.getTimeInMillis()/1000));
    }
//-------------------------------------------------------------------------------------------------
}
//=================================================================================================