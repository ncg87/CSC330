package com.example.talkingpicturelistcopy;

import android.app.DialogFragment;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.example.talkingpicturelist.R;
import android.widget.ImageView;

//==================================================================================================
public class UIDialogsCustomFragment extends DialogFragment implements View.OnClickListener {
    //----------------------------------------------------------------------------------------------
    View dialogView;
    //----------------------------------------------------------------------------------------------
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        //shows dialog
        dialogView = inflater.inflate(R.layout.dialogui,container);

        ImageView dialogImage = dialogView.findViewById(R.id.theDialogImage);
        //gets image
        String picture = this.getArguments().getString(getResources().getString(R.string.imageUriKey));
        //allows dismiss button to be clicked
        ((Button)dialogView.findViewById(R.id.dismiss)).setOnClickListener(this);
        //set image
        dialogImage.setImageURI(Uri.parse(picture));

        return dialogView;
    }
    //----------------------------------------------------------------------------------------------
    @Override
    public void onClick(View view) {

        //gets activity
        StopTalking myActivity;
        myActivity = (StopTalking) getActivity();

        if(view.getId() == R.id.dismiss){
            //closes fragment and stops text to speech
            dismiss();
            myActivity.stopTalking();
        }
    }
    //----------------------------------------------------------------------------------------------
    public interface StopTalking{
        public void stopTalking();
    }
}
//==================================================================================================