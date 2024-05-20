package com.example.showaphoto;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Button;
//==================================================================================================
public class UIDialogsCustomFragment extends DialogFragment implements View.OnClickListener {
    //----------------------------------------------------------------------------------------------
    View dialogView;
    //----------------------------------------------------------------------------------------------
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        dialogView = inflater.inflate(R.layout.dialog,container);
        ImageView theTV = dialogView.findViewById(R.id.ImageTV);
        int picture = this.getArguments().getInt("image");
        ((Button)dialogView.findViewById(R.id.dismiss)).setOnClickListener(this);
        theTV.setImageResource(picture);
        return(dialogView);
    }
    //----------------------------------------------------------------------------------------------
    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.dismiss){
            dismiss();
        }
    }
    //----------------------------------------------------------------------------------------------
}
//==================================================================================================