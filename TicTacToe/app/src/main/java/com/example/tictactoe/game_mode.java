package com.example.tictactoe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
//==================================================================================================
public class game_mode extends AppCompatActivity {
    //----------------------------------------------------------------------------------------------
    private int playTime;
    private ProgressBar myProgressBar;
    private String player1Name;
    private String player2Name;
    private ImageButton player1;
    private ImageButton player2;
    private TextView playerTurn;
    private int[][] recordTurn;
    private Intent returnIntent;
    private int turns;
    private Uri player1URI;
    private Uri player2URI;
    private double dividingLine;

    //----------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_mode);

        playerTurn = (TextView)findViewById(R.id.player_turn);
        myProgressBar = findViewById(R.id.progress_bar);
        player1 = findViewById(R.id.player1_image);
        player2 = findViewById(R.id.player2_image);
        recordTurn = new int[3][3];
        turns = 0;

        playTime = this.getIntent().getIntExtra("playTime",50);
        player1Name = this.getIntent().getStringExtra("player1Name");
        player2Name = this.getIntent().getStringExtra("player2Name");
        player1URI = this.getIntent().getParcelableExtra("player1Image");
        player2URI = this.getIntent().getParcelableExtra("player2Image");
        double starter = Math.random();
        dividingLine = this.getIntent().getDoubleExtra("dividingLine",0);

        if(player1URI != null){
            player1.setImageURI(player1URI);
        }if(player2URI != null){
            player2.setImageURI(player2URI);
        }
        myProgressBar.setProgress(myProgressBar.getMax());
        if(dividingLine > starter){
            player2.setVisibility(View.INVISIBLE);
            playerTurn.setText(player1Name);
            dividingLine = dividingLine -0.1;
        }else{
            player1.setVisibility(View.INVISIBLE);
            playerTurn.setText(player2Name);
            dividingLine = dividingLine +0.1;
        }
        myProgressor.run();
    }
    //----------------------------------------------------------------------------------------------
    public void myClickHandler(View view){
        int player = 2;
        if(getTurn().equals(player1Name))
            player = 1;
        switch (view.getId()){
            case R.id.a1:
                buttonHandler(findViewById(R.id.a1),player,1,1);
                break;
            case R.id.a2:
                buttonHandler(findViewById(R.id.a2),player,1,2);
                break;
            case R.id.a3:
                buttonHandler(findViewById(R.id.a3),player,1,3);
                break;
            case R.id.b1:
                buttonHandler(findViewById(R.id.b1),player,2,1);
                break;
            case R.id.b2:
                buttonHandler(findViewById(R.id.b2),player,2,2);
                break;
            case R.id.b3:
                buttonHandler(findViewById(R.id.b3),player,2,3);
                break;
            case R.id.c1:
                buttonHandler(findViewById(R.id.c1),player,3,1);
                break;
            case R.id.c2:
                buttonHandler(findViewById(R.id.c2),player,3,2);
                break;
            case R.id.c3:
                buttonHandler(findViewById(R.id.c3),player,3,3);
                break;
            default:
                break;
        }
    }
    //----------------------------------------------------------------------------------------------
    private Handler myHandler = new Handler();
    private final Runnable myProgressor = new Runnable() {
        @Override
        public void run() {
            myProgressBar.setProgress(myProgressBar.getProgress()-playTime);
            if(myProgressBar.getProgress() <= 0){
                changeTurn();
            }
            if(!myHandler.postDelayed(myProgressor,playTime)){
                Log.e("ERROR","Cannot postDelayed");
            }
        }
    };
    //----------------------------------------------------------------------------------------------
    protected void onDestroy() {

        super.onDestroy();
        myHandler.removeCallbacks(myProgressor);
    }
    //----------------------------------------------------------------------------------------------
    public void changeTurn(){
        if(getTurn().equals(player1Name)){
            playerTurn.setText(player2Name);
            myProgressBar.setProgress(myProgressBar.getMax());
            player1.setVisibility(View.INVISIBLE);
            player2.setVisibility(View.VISIBLE);

        }else{
            playerTurn.setText(player1Name);
            myProgressBar.setProgress(myProgressBar.getMax());
            player2.setVisibility(View.INVISIBLE);
            player1.setVisibility(View.VISIBLE);
        }
    }
    //----------------------------------------------------------------------------------------------
    public String getTurn(){
        return playerTurn.getText().toString();
    }
    //----------------------------------------------------------------------------------------------
    public void buttonHandler(ImageButton buttonSelected, int player, int column, int row){
        recordTurn[column-1][row-1] = player;
        buttonSelected.setClickable(false);
        ++turns;
        //check player
        if(getTurn().equals(player1Name)){
            if(player1URI != null){
                buttonSelected.setImageURI(player1URI);
            }else {
                buttonSelected.setBackground(getResources().getDrawable(R.drawable.player1));
            }
        }else{
            if(player2URI != null) {
                buttonSelected.setImageURI(player2URI);
            }else{
                buttonSelected.setBackground(getResources().getDrawable(R.drawable.player2));
            }
        }
        //check if won
        if(checkWin(player)){
            returnIntent = new Intent();
            returnIntent.putExtra("winner",player);
            returnIntent.putExtra("dividingLine",dividingLine);
            setResult(RESULT_OK,returnIntent);
            finish();
        }
        //check if draw
        if(turns >= 9){
            setResult(RESULT_CANCELED,returnIntent);
            finish();
        }
        myProgressBar.setProgress(0);

    }
    //----------------------------------------------------------------------------------------------
    public boolean checkWin(int player){
        if (recordTurn[0][0] == player && recordTurn[0][1] == player && recordTurn[0][2] == player) {
            return true;
        } else if (recordTurn[1][0] == player && recordTurn[1][1] == player && player == recordTurn[1][2]) {
            return true;
        } else if (recordTurn[2][0] == player && recordTurn[2][1] == player && player == recordTurn[2][2]) {
            return true;
        } else if (recordTurn[0][0] == player && recordTurn[1][0] == player && player == recordTurn[2][0]) {
            return true;
        } else if (recordTurn[0][1] == player && recordTurn[1][1] == player && player == recordTurn[2][1]) {
            return true;
        } else if (recordTurn[0][2] == player && recordTurn[1][2] == player && player == recordTurn[2][2]) {
            return true;
        } else if (recordTurn[0][0] == player && recordTurn[1][1] == player && player == recordTurn[2][2]) {
            return true;
        } else if (recordTurn[0][2] == player && recordTurn[1][1] == player && player == recordTurn[2][0]) {
            return true;
        }
        return false;
    }
}
//==================================================================================================