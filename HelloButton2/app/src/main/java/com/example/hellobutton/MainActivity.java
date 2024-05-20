package com.example.hellobutton;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
//-------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
//----------------------------------------------------------
    public void myClickHandler(View view) {
        Vibrator buzzer;
        final long[] menuBuzz = {0, 250, 50, 250, 500, 250, 50, 250, 500, 250, 50, 250, 500, 250, 50, 250};
        switch (view.getId()) {
            case R.id.toast:
                Log.i("IN myClickHandler","Toast has been chosen");
                Toast.makeText(this, "Toast is Ready!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.buzz:
                Log.i("IN myClickHandler","Buzz has been chosen");
                buzzer = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                buzzer.vibrate(menuBuzz, -1);
                break;
            default:
                return;
        }
    }
//---------------------------------------------------------
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        Log.i("IN onCreateOptionsMenu","The menu has been created");
        return (true);
    }
//---------------------------------------------------------------------
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent nextActivity;
        if(item.getItemId() == R.id.hellobutton){
            Log.i("In onOptionsItemSelected","HelloButton has been selected");
            nextActivity = new Intent(this, HelloButton.class);
            startActivity(nextActivity);
        }
        return super.onOptionsItemSelected(item);
    }
}