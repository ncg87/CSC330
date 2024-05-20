package com.example.uidialogscustomfragments;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

//=================================================================================================
public class MainActivity extends AppCompatActivity {
    //-------------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    //-------------------------------------------------------------------------------------------------
    public void myClickHandler(View view) {

        switch (view.getId()) {
            case R.id.show_dialog:
                (new UIDialogsCustomFragment()).show(getFragmentManager(),"my_fragment");
                break;
            case R.id.exit:
                finish();
                break;
            default:
                break;
        }
    }
//-------------------------------------------------------------------------------------------------
}
//=================================================================================================