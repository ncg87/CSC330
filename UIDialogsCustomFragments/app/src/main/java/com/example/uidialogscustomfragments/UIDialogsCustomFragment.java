package com.example.uidialogscustomfragments;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ViewFlipper;

//=================================================================================================
public class UIDialogsCustomFragment extends DialogFragment implements View.OnClickListener {
    //-------------------------------------------------------------------------------------------------
    View dialogView;
    //-------------------------------------------------------------------------------------------------
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,
                             Bundle savedInstanceState) {

        getDialog().setTitle(R.string.see_button_label);
        dialogView = inflater.inflate(R.layout.dialog,container);
        ((Button)dialogView.findViewById(R.id.start_show)).setOnClickListener(this);
        ((Button)dialogView.findViewById(R.id.stop_show)).setOnClickListener(this);
        ((Button)dialogView.findViewById(R.id.end_show)).setOnClickListener(this);
        return(dialogView);
    }
    //-------------------------------------------------------------------------------------------------
    @Override
    public void onClick(View view) {

        ViewFlipper flipperInDialog;

        flipperInDialog = dialogView.findViewById(R.id.flipper);

        switch (view.getId()) {
            case R.id.start_show:
                flipperInDialog.startFlipping();
                break;
            case R.id.stop_show:
                flipperInDialog.stopFlipping();
                break;
            case R.id.end_show:
                dismiss();
                break;
            default:
                break;
        }
    }
//-------------------------------------------------------------------------------------------------
}
//=================================================================================================