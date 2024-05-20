package com.example.hidingpictures;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
//==================================================================================================

public class MainActivity extends AppCompatActivity {
    //----------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        registerForContextMenu(findViewById(R.id.image2));
    }
    //----------------------------------------------------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu,menu);
        return(true);
    }
    //----------------------------------------------------------------------------------------------
    public boolean onOptionsItemSelected(MenuItem item) {
        TextView theText;
        ImageView theImage;

        theText = findViewById(R.id.opinion);
        theImage = findViewById(R.id.image2);

        switch  (item.getItemId()){
            case R.id.like:
                theText.setText(R.string.like);
                return true;
            case R.id.indifferent:
                theText.setText(R.string.indifferent);
                return true;
            case R.id.dislike:
                theText.setText(R.string.dislike);
                return true;
            case R.id.quit:
                finish();
                return true;
            case R.id.reset:
                theText.setText(R.string.indifferent);
                theImage.setVisibility(View.VISIBLE);
                return true;
            default:
                return(super.onOptionsItemSelected(item));
        }

    }
    //----------------------------------------------------------------------------------------------
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {

        super.onCreateContextMenu(menu,view,menuInfo);
        getMenuInflater().inflate(R.menu.context_menu,menu);
    }
    //----------------------------------------------------------------------------------------------
    public boolean onContextItemSelected(MenuItem item) {
        ImageView theImage;
        theImage = findViewById(R.id.image2);
        switch (item.getItemId()){
            case R.id.invisible:
                theImage.setVisibility(View.INVISIBLE);
                return true;
            case R.id.disappear:
                theImage.setVisibility(View.GONE);
                return true;
            default:
                return(super.onOptionsItemSelected(item));
        }

    }
//--------------------------------------------------------------------------------------------------
}
//==================================================================================================