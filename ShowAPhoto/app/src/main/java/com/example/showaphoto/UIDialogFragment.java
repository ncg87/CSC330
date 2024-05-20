package com.example.showaphoto;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.DialogFragment;

//================================================================
public class UIDialogFragment extends DialogFragment {
    //-------------------------------------------------------------------------------
    public Dialog onCreateDialog(Bundle savedInstanceState){
        
        AlertDialog.Builder dialogBuilder;
        
        dialogBuilder = new AlertDialog.Builder(getActivity());
        
        dialogBuilder.setTitle("Pick a Movie");
        dialogBuilder.setItems(R.array.movies, listListener);
        return (dialogBuilder.create());
    }
    //-------------------------------------------------------------
    public interface SetTVPicture {
        public void setTVPicture(int resourceId);
    }
    //------------------------------------------------------------
    private DialogInterface.OnClickListener listListener =
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,int indexClicked) {
                    String[]movies;
                    SetTVPicture myActivity;
                    myActivity = (SetTVPicture) getActivity();
                    switch (indexClicked) {
                        case 0:
                            myActivity.setTVPicture(R.drawable.pulp_fiction);
                            break;
                        case 1:
                            myActivity.setTVPicture(R.drawable.djengo);
                            break;
                        case 2:
                            myActivity.setTVPicture(R.drawable.pianist);
                            break;
                        case 3:
                            myActivity.setTVPicture(R.drawable.inglorious_bastards);
                            break;
                        default:
                            break;
                    }
                }
            };
//------------------------------------------------------------
    
}
