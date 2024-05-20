package com.example.tictactoe;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

//==================================================================================================
public class MainActivity extends AppCompatActivity {
    //----------------------------------------------------------------------------------------------
    private RatingBar player1Wins;
    private RatingBar player2Wins;
    private int playTime;
    private Button playButton;
    private Intent nextActivity;
    private ImageButton player1Image;
    private ImageButton player2Image;
    boolean isPlayer1Image;
    double dividingLine;
    //----------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        registerForContextMenu(findViewById(R.id.player1_image));
        registerForContextMenu(findViewById(R.id.player2_image));

        player1Wins = findViewById(R.id.wins_player1);
        player2Wins = findViewById(R.id.wins_player2);
        playButton = findViewById(R.id.play_button);
        player1Image = findViewById(R.id.player1_image);
        player2Image = findViewById(R.id.player2_image);
        playTime = getResources().getInteger(R.integer.five_seconds);
        dividingLine = 0.5;
        nextActivity = new Intent(this, game_mode.class);
    }
    //----------------------------------------------------------------------------------------------
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return(true);
    }
    //----------------------------------------------------------------------------------------------
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu,view,menuInfo);
        startGallery.launch("image/*");
        if(view.equals(player1Image)){
            isPlayer1Image = true;
        }else{
            isPlayer1Image = false;
        }

    }
    //----------------------------------------------------------------------------------------------
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.reset:
                player1Wins.setRating(0);
                player2Wins.setRating(0);
                playButton.setVisibility(View.VISIBLE);
                return true;
            case R.id.one_second:
                playTime = getResources().getInteger(R.integer.one_second);
                return true;
            case R.id.two_second:
                playTime = getResources().getInteger(R.integer.two_seconds);
                return true;
            case R.id.five_second:
                playTime = getResources().getInteger(R.integer.five_seconds);
                return true;
            case R.id.ten_second:
                playTime = getResources().getInteger(R.integer.ten_seconds);
                return true;
            default:
                return(super.onOptionsItemSelected(item));
        }
    }
    //----------------------------------------------------------------------------------------------
    public void myClickHandler(View view){
        EditText player1 = findViewById(R.id.name1);
        EditText player2 = findViewById(R.id.name2);
        if(view.getId() == R.id.play_button){
            nextActivity.putExtra("playTime",playTime);
            nextActivity.putExtra("player1Name",player1.getText().toString());
            nextActivity.putExtra("player2Name",player2.getText().toString());
            nextActivity.putExtra("dividingLine",dividingLine);
            playerWins.launch(nextActivity);
        }
    }
    //----------------------------------------------------------------------------------------------
    ActivityResultLauncher<Intent> playerWins = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK){
                        int winner = result.getData().getIntExtra("winner", 0);
                        dividingLine = result.getData().getDoubleExtra("dividingLine",0);
                        if(winner == 1) {
                            player1Wins.setRating(player1Wins.getRating() + 1);
                            if (player1Wins.getRating() >= 5) {
                                playButton.setVisibility(View.INVISIBLE);
                            }
                        }else {
                            player2Wins.setRating(player2Wins.getRating() + 1);
                            if (player2Wins.getRating() >= 5) {
                                playButton.setVisibility(View.INVISIBLE);
                            }
                        }
                    }
                }
            });
    //----------------------------------------------------------------------------------------------
    ActivityResultLauncher<String> startGallery = registerForActivityResult(
            new ActivityResultContracts.GetContent(),new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri resultUri) {
                    if (resultUri != null) {
                        if(isPlayer1Image) {
                            player1Image.setImageURI(resultUri);
                            nextActivity.putExtra("player1Image", resultUri);
                        }else{
                            player2Image.setImageURI(resultUri);
                            nextActivity.putExtra("player2Image",resultUri);
                        }
                    }
                }
            });
}