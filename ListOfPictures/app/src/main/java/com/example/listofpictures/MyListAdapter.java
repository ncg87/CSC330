package com.example.listofpictures;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleAdapter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
//==================================================================================================
public class MyListAdapter extends SimpleAdapter implements SimpleAdapter.ViewBinder {
    //----------------------------------------------------------------------------------------------
    private Context viewContext;
    int[]faceIds;
    //----------------------------------------------------------------------------------------------
    public MyListAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] keyNames, int[] fieldIds) {
        super(context, data, resource, keyNames, fieldIds);

        viewContext = context;
        faceIds = new int[getCount()];
        Arrays.fill(faceIds,R.drawable.happy);
        setViewBinder(this);
    }
    //----------------------------------------------------------------------------------------------
    @Override
    public boolean setViewValue(View view, Object o, String s) {
        return false;
    }
    //----------------------------------------------------------------------------------------------
    public int swapFace(int position){
        if(faceIds[position]==R.drawable.happy){
           faceIds[position] = R.drawable.sad;
        }else{
            faceIds[position] = R.drawable.happy;
        }
        return faceIds[position];
    }
    //----------------------------------------------------------------------------------------------
    public View getView(int position, View convertView, ViewGroup parent){
        ImageView image;
        View view;

        view = super.getView(position,convertView,parent);
        image = (ImageView)view.findViewById(R.id.country_picture);
        image.setImageDrawable(viewContext.getDrawable(R.drawable.happy));

        return view;
    }
}
//==================================================================================================
