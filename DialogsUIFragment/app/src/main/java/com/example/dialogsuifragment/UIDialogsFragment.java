package com.example.dialogsuifragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dialogsuifragment.R;

//=================================================================================================
public class UIDialogsFragment extends DialogFragment {
    //-------------------------------------------------------------------------------------------------
    private boolean[] foodOrder;
    //-------------------------------------------------------------------------------------------------
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder dialogBuilder;

        dialogBuilder = new AlertDialog.Builder(getActivity());

        switch (this.getArguments().getInt("dialog_type")) {
            case MainActivity.YES_NO_DIALOG:
                dialogBuilder.setMessage("Exit?");
                dialogBuilder.setPositiveButton("Yes",yesNoListener);
                dialogBuilder.setNegativeButton("No",yesNoListener);
//----Have to set the fragment not cancellable, rather than the dialog
                setCancelable(false);
                break;
            case MainActivity.LIST_DIALOG:
                dialogBuilder.setTitle("Pick a president");
                dialogBuilder.setItems(R.array.presidents,listListener);
                break;
            case MainActivity.RADIOS_DIALOG:
                dialogBuilder.setTitle("Which TV Show?");
                dialogBuilder.setSingleChoiceItems(R.array.tv_shows,-1,radiosListener);
                dialogBuilder.setPositiveButton("Watch", null);
                break;
            case MainActivity.CHECKBOXES_DIALOG:
                foodOrder = new boolean[getResources().getStringArray(R.array.menu).length];
                dialogBuilder.setTitle("Order your Food");
                dialogBuilder.setMultiChoiceItems(R.array.menu,null,checkboxesListener);
                dialogBuilder.setPositiveButton("Eat",eatListener);
                break;
            default:
                break;
        }
        return (dialogBuilder.create());
    }
    //-------------------------------------------------------------------------------------------------
//----Have to do these as anonymous inner classes (rather than implementing
//----the interface in this class) because I need four different ones.
    private DialogInterface.OnClickListener listListener =
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,int indexClicked) {

                    String[] presidents;

                    presidents = getResources().getStringArray(R.array.presidents);
                    Toast.makeText(getActivity().getApplicationContext(),
                            "You voted for " + presidents[indexClicked],Toast.LENGTH_SHORT).show();
                }
            };
    //-------------------------------------------------------------------------------------------------
    public interface SetTVPicture {
        public void setTVPicture(int resourceId);
    }
    //-------------------------------------------------------------------------------------------------
    private DialogInterface.OnClickListener radiosListener =
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,int indexClicked) {

                    SetTVPicture myActivity;
                    myActivity = (SetTVPicture)getActivity();

                    /*switch (indexClicked) {
                        case 0:
                            myActivity.setTVPicture(R.drawable.deadliestcatch);
                            break;
                        case 1:
                            myActivity.setTVPicture(R.drawable.archer);
                            break;
                        case 2:
                            myActivity.setTVPicture(R.drawable.topgear);
                            break;
                        case 3:
                            myActivity.setTVPicture(R.drawable.mash);
                            break;
                        case 4:
                            myActivity.setTVPicture(R.drawable.twoandahalfmen);
                            break;
                        case 5:
                            myActivity.setTVPicture(R.drawable.bigcatdiary);
                            break;
                        case 6:
                            myActivity.setTVPicture(-1);
                            break;
                        default:
                            break;
                    }*/
                }
            };
    //-------------------------------------------------------------------------------------------------
    private DialogInterface.OnMultiChoiceClickListener checkboxesListener =
            new DialogInterface.OnMultiChoiceClickListener() {

                public void onClick(DialogInterface dialog,int indexClicked,boolean selected) {

                    foodOrder[indexClicked] = selected;
                }
            };
    //-------------------------------------------------------------------------------------------------
    private DialogInterface.OnClickListener eatListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog,int indexClicked) {

            String[] menu;
            String order = "";
            int itemIndex;

            menu = getResources().getStringArray(R.array.menu);
            for (itemIndex=0;itemIndex < menu.length;itemIndex++) {
                if (foodOrder[itemIndex]) {
                    if (order.length() > 0) {
                        order += " and ";
                    }
                    order += menu[itemIndex];
                }
            }
//----If I was groovy I would define another interface to have this done back
//----in the main activity.
            ((TextView)getActivity().findViewById(R.id.plate)).setText(order);
        }
    };
    //-------------------------------------------------------------------------------------------------
    private DialogInterface.OnClickListener yesNoListener =
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,int whatWasClicked) {

                    switch (whatWasClicked) {
                        case DialogInterface.BUTTON_POSITIVE:
                            getActivity().finish();
                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            dismiss();
                            break;
                        default:
                            break;
                    }
                }
            };
//-------------------------------------------------------------------------------------------------
}
//=================================================================================================