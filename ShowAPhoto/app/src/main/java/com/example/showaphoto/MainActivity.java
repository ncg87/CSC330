package com.example.showaphoto;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

//==================================================================================================
public class MainActivity extends AppCompatActivity implements UIDialogFragment.SetTVPicture{
//--------------------------------------------------------------------------------------------------
    public static final int LIST_DIALOG = 2;
//--------------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    //----------------------------------------------------------------------------------------------
    public void myClickHandler(View view){

        Bundle bundleToFragment;
        UIDialogFragment theDialogFragment;

        bundleToFragment = new Bundle();

        switch(view.getId()){
            case R.id.dialog_button:
                theDialogFragment = new UIDialogFragment();
                theDialogFragment.setArguments(bundleToFragment);
                theDialogFragment.show(getFragmentManager(),"my_fragment");
                break;
            case R.id.exit:
                finish();
                break;
            default:
                break;
        }

    }
//--------------------------------------------------------------------------------------------------
    @Override
    public void setTVPicture(int resourceId) {
        UIDialogsCustomFragment customFragment;
        Bundle bundleToFragment;

        bundleToFragment = new Bundle();
        customFragment = new UIDialogsCustomFragment();

        bundleToFragment.putInt("image",resourceId);
        customFragment.setArguments(bundleToFragment);
        customFragment.show(getFragmentManager(),"my_fragment");


    }
    //----------------------------------------------------------------------------------------------
}
//==================================================================================================