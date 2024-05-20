package com.example.uimenus;

import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.uimenus.R;

//=================================================================================================
public class MainActivity extends AppCompatActivity {
    //-------------------------------------------------------------------------------------------------
    private int currentSizeId;
    //-------------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        registerForContextMenu(findViewById(R.id.the_text));
        currentSizeId = R.id.medium;
    }
    //-------------------------------------------------------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu,menu);
        return(true);
    }
    //-------------------------------------------------------------------------------------------------
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        TextView theText;
        ImageView theImage;

        theText = findViewById(R.id.the_text);
        theImage = findViewById(R.id.happy_sad_image);

        switch (item.getItemId()) {
            case R.id.make_happy:
                theImage.setImageResource(R.drawable.happy);
                return(true);
            case R.id.make_sad:
                theImage.setImageResource(R.drawable.sad);
                return(true);
            case R.id.white:
                theText.setTextColor(Color.WHITE);
                return(true);
            case R.id.red:
                theText.setTextColor(Color.RED);
                return(true);
            case R.id.green:
                theText.setTextColor(Color.GREEN);
                return(true);
            case R.id.blue:
                theText.setTextColor(Color.BLUE);
                return(true);
            default:
                return(super.onOptionsItemSelected(item));
        }
    }
    //-------------------------------------------------------------------------------------------------
    public void onCreateContextMenu(ContextMenu menu,View view,
                                    ContextMenu.ContextMenuInfo menuInfo) {

        super.onCreateContextMenu(menu,view,menuInfo);
        getMenuInflater().inflate(R.menu.context_menu,menu);
        menu.findItem(currentSizeId).setChecked(true);
    }
    //-------------------------------------------------------------------------------------------------
    public boolean onContextItemSelected(MenuItem item) {

        TextView theText;

        theText = findViewById(R.id.the_text);

        currentSizeId = item.getItemId();
        switch (item.getItemId()) {
            case R.id.small:
                theText.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                        getResources().getDimension(R.dimen.small_text_size));
                return(true);
            case R.id.medium:
                theText.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                        getResources().getDimension(R.dimen.middle_text_size));
                return(true);
            case R.id.large:
                theText.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                        getResources().getDimension(R.dimen.large_text_size));
                return(true);
            default:
                return(super.onOptionsItemSelected(item));
        }
    }
//-------------------------------------------------------------------------------------------------
}
//=================================================================================================