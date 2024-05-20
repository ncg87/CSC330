package com.example.adapterelements;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import androidx.core.content.res.ResourcesCompat;

//=================================================================================================
public class MyGridAdapter extends SimpleAdapter implements SimpleAdapter.ViewBinder {
    //-------------------------------------------------------------------------------------------------
    private boolean[] picture_visible;
    private Context viewContext;
    //-------------------------------------------------------------------------------------------------
    public MyGridAdapter(Context context,List<? extends Map<String, ?>> data,
                         int resource,String[] keyNames,int[] fieldIds) {

        super(context,data,resource,keyNames,fieldIds);

        viewContext = context;
        picture_visible = new boolean[getCount()];
        Arrays.fill(picture_visible,true);
        setViewBinder(this);
    }
    //-------------------------------------------------------------------------------------------------
    public void hideCountry(int position) {

        picture_visible[position] = false;
    }
    //-------------------------------------------------------------------------------------------------
//----Need to override getView so image visibility is correct
    @Override
    public View getView(int position,View convertView,ViewGroup parent) {

        ImageView image;
        View view;

        view = super.getView(position,convertView,parent);
//----Comment out from here to end of if-else, then scroll the grid to see things go wrong
        image = (ImageView)view.findViewById(R.id.country_picture);
        if (picture_visible[position]) {
            image.setVisibility(View.VISIBLE);
        } else {
            image.setVisibility(View.INVISIBLE);
        }
        return(view);
    }
    //-------------------------------------------------------------------------------------------------
//----Need to implement this when data type is not constant text or image. Here the image is
//----random.
    @Override
    public boolean setViewValue (View view,Object data,String textRepresentation) {

        switch (view.getId()) {
            case R.id.country_name:
                ((TextView)view).setText((String)data);
                break;
            case R.id.country_picture:
                if (Math.random() < 0.5) {
                    ((ImageView)view).setImageDrawable(ResourcesCompat.getDrawable(
                            viewContext.getResources(),R.drawable.happy,null));
                } else {
                    ((ImageView)view).setImageDrawable(ResourcesCompat.getDrawable(
                            viewContext.getResources(),R.drawable.sad,null));
                }
        }
        return(true);
    }
//-------------------------------------------------------------------------------------------------
}
//=================================================================================================