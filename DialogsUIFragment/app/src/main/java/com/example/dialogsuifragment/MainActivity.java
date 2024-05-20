package com.example.dialogsuifragment;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dialogsuifragment.R;
import com.example.dialogsuifragment.UIDialogsFragment;

//=================================================================================================
public class MainActivity extends AppCompatActivity implements UIDialogsFragment.SetTVPicture {
    //-------------------------------------------------------------------------------------------------
    public static final int YES_NO_DIALOG = 1;
    public static final int LIST_DIALOG = 2;
    public static final int RADIOS_DIALOG = 3;
    public static final int CHECKBOXES_DIALOG = 4;
    //-------------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    //-------------------------------------------------------------------------------------------------
    public void myClickListener(View view) {

        Bundle bundleToFragment;
        UIDialogsFragment theDialogFragment;

        bundleToFragment = new Bundle();

        switch (view.getId()) {
            case R.id.list_button:
                bundleToFragment.putInt("dialog_type",LIST_DIALOG);
                break;
            case R.id.radios_button:
                bundleToFragment.putInt("dialog_type",RADIOS_DIALOG);
                break;
            case R.id.checkboxes_button:
                bundleToFragment.putInt("dialog_type",CHECKBOXES_DIALOG);
                break;
            case R.id.yes_no_button:
                bundleToFragment.putInt("dialog_type",YES_NO_DIALOG);
                break;
            default:
                break;
        }
        theDialogFragment = new UIDialogsFragment();
        theDialogFragment.setArguments(bundleToFragment);
        theDialogFragment.show(getFragmentManager(),"my_fragment");
    }
    //-------------------------------------------------------------------------------------------------
    public void setTVPicture(int resourceId) {

        ImageView theTV;

        theTV = (ImageView)findViewById(R.id.tv);
        if (resourceId == -1) {
            theTV.setVisibility(View.INVISIBLE);
        } else {
            theTV.setVisibility(View.VISIBLE);
            theTV.setImageResource(resourceId);
        }
    }
//-------------------------------------------------------------------------------------------------
}
//=================================================================================================