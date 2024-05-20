package com.example.adapterelements;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.appcompat.app.AppCompatActivity;

//=================================================================================================
public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener,
        AdapterView.OnItemSelectedListener {
    //-------------------------------------------------------------------------------------------------
    private MyGridAdapter gridAdapter;
    //-------------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        ArrayAdapter<String> arrayAdapter;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        arrayAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.countries_array));
//----Optionally, make the list look nicer
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ((Spinner)findViewById(R.id.the_spinner)).setAdapter(arrayAdapter);
        ((Spinner)findViewById(R.id.the_spinner)).setOnItemSelectedListener(this);

        gridAdapter = makeGridAdapter();
        ((GridView)findViewById(R.id.the_grid)).setAdapter(gridAdapter);
        ((GridView)findViewById(R.id.the_grid)).setOnItemClickListener(this);
    }
    //-------------------------------------------------------------------------------------------------
    public void onItemSelected(AdapterView<?> parent,View view,int position,long id) {

        if (position > 0) {
            Toast.makeText(this,"You selected " + parent.getItemAtPosition(position),
                    Toast.LENGTH_SHORT).show();
        }
    }
    //-------------------------------------------------------------------------------------------------
    public void onNothingSelected(AdapterView<?> parent) {
    }
    //-------------------------------------------------------------------------------------------------
    private MyGridAdapter makeGridAdapter() {

        String[] countries;
        ArrayList<HashMap<String,Object>> gridItems;
        HashMap<String,Object> oneItem;
        int index;
        String[] fromHashMapFieldNames = {"name","picture"};
        int[] toGridFieldIds = {R.id.country_name,R.id.country_picture};

        countries = getResources().getStringArray(R.array.countries_array);
        countries[0] = "USA";
        gridItems = new ArrayList<>();
        for (index = 0; index < countries.length; index++) {
            oneItem = new HashMap<>();
            oneItem.put("name",countries[index]);
            oneItem.put("picture",R.drawable.happy);
            gridItems.add(oneItem);
        }

        return(new MyGridAdapter(this,gridItems,R.layout.grid_cell,fromHashMapFieldNames,
                toGridFieldIds));
    }
    //-------------------------------------------------------------------------------------------------
    public void onItemClick(AdapterView<?> parent,View view,int position,long id) {

        String message;

        view.findViewById(R.id.country_picture).setVisibility(View.INVISIBLE);
        gridAdapter.hideCountry(position);
        message = String.format("Country = %s (%d) killed",
                ((TextView) view.findViewById(R.id.country_name)).getText(),position);
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }
//-------------------------------------------------------------------------------------------------

}
//=================================================================================================