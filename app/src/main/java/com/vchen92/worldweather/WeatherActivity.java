package com.vchen92.worldweather;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;


public class WeatherActivity extends AppCompatActivity {

    private static String city;

    public static void setCity(String city){
        WeatherActivity.city = city;
    }

    public static String getCity(){
        if(city == null) {return "Unknown";}
        return city;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new WeatherFragment())
                    .commit();
        }

        //Get location from MapsActivity
        //Set location to focus city
        //Vancouver as default city
        Bundle mapData = getIntent().getExtras();
        if(mapData == null){
            setCity("Vancouver");
            return;
        }
        String mapLocation = mapData.getString("city");
        setCity(mapLocation);
    }

    //OnClick to change to MapsActivity
    public void toMapView(View view) {
        Intent c = new Intent(this, MapsActivity.class);

        c.putExtra("city", WeatherActivity.getCity());

        startActivity(c);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the map; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.weather, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.change_city){
            showInputDialog();
        }
        return false;

    }

    //Dialog to ask user for focus city
    private void
    showInputDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Change city");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton("Go", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                changeCity(input.getText().toString());
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    //Change focus city
    public void changeCity(String city){
        setCity(city);
        WeatherFragment wf = (WeatherFragment)getSupportFragmentManager()
                .findFragmentById(R.id.container);
        wf.changeCity(city);
    }

}