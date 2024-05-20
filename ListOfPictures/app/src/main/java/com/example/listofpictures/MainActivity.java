package com.example.listofpictures;

import androidx.appcompat.app.AppCompatActivity;

import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;

//==================================================================================================
public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {
//--------------------------------------------------------------------------------------------------
    private MyListAdapter listAdapter;
    private ImageView theImage;
    //----------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listAdapter = makeListAdapter();
        theImage = findViewById(R.id.the_image);
        ((ListView)findViewById(R.id.the_list)).setAdapter(listAdapter);
        ((ListView)findViewById(R.id.the_list)).setOnItemClickListener(this);
        theImage.setImageDrawable(getDrawable(R.drawable.happy));

    }
    //----------------------------------------------------------------------------------------------
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        ImageView countryPicture;
        countryPicture = view.findViewById(R.id.country_picture);
        id = listAdapter.swapFace(position);
        theImage.setImageResource((int)id);
        countryPicture.setImageResource((int)id);


    }
    //----------------------------------------------------------------------------------------------
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }
    //----------------------------------------------------------------------------------------------
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
    //----------------------------------------------------------------------------------------------
    private MyListAdapter makeListAdapter(){
        String[] countries;
        int index;
        ArrayList<HashMap<String,Object>> listItems;
        HashMap<String,Object> singleItem;
        String[] fromHashMapFieldNames = {"name", "picture"};
        int[]toListFieldIds = {R.id.country_name,R.id.country_picture};


        countries = getResources().getStringArray(R.array.countries_array);
        countries[0] = "USA";
        listItems = new ArrayList<>();
        for(index = 0; index < countries.length; index++){
            singleItem = new HashMap<>();
            singleItem.put("name",countries[index]);
            singleItem.put("picture",R.drawable.happy);
            listItems.add(singleItem);
        }

        return(new MyListAdapter(this,listItems,R.layout.list_cell,fromHashMapFieldNames,toListFieldIds));
    }
}
//==================================================================================================