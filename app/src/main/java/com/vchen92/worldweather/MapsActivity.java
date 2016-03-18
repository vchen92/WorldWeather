package com.vchen92.worldweather;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends AppCompatActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private static String city;

    //Set the focus city
    public static void setCity(String city) {
        MapsActivity.city = city;
    }

    //Get current focused city
    public static String getCity() {
        if (city == null) {
            return "Unknown";
        }
        return city;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();

        //Get location from WeatherActivity
        //Set to current city
        //Vancouver as default city
        Bundle weatherData = getIntent().getExtras();
        if (weatherData == null) {
            changeCity("vancouver");
            return;
        }
        String weatherLocation = weatherData.getString("city");
        changeCity(weatherLocation);
    }

    //OnClick to change to WeatherActivity
    public void toWeatherView(View view) {
        Intent i = new Intent(this, WeatherActivity.class);

        i.putExtra("city", MapsActivity.getCity());

        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the map; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.change_location) {
            showInputDialog();
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    //Changes the map type (normal or satellite)
    public void changeType(View view) {
        if (mMap.getMapType() == GoogleMap.MAP_TYPE_NORMAL) {
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        } else
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    //Map setup default to Vancouver, CA
    private void setUpMap() {
        mMap.addMarker(new MarkerOptions().position(new LatLng(49.2827, -123.1207)).title("Marker"));
        mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(49.2827, -123.1207)));
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {
                revGeoCode(point);
            }
        });
    }

    //Dialog for user input of desired city
    private void
    showInputDialog() {
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

    //Convert city into geo-coordinates and focus on map
    public void changeCity(String location) {
        setCity(location);
        List<Address> addressList = null;

        if (location != null || location.equals("")) {
            Geocoder geocoder = new Geocoder(this);
            try {
                addressList = geocoder.getFromLocationName(location, 1);

            } catch (IOException e) {
                e.printStackTrace();
            }

            Address address = addressList.get(0);
            createMarker(new LatLng(address.getLatitude(), address.getLongitude()));
        }
    }

    //Convert geo-coordinates of marker to closest locality
    //Set locality to focused city
    //Use locality for marker title
    public void revGeoCode(LatLng point) {
        double lat = point.latitude;
        double lng = point.longitude;
        List<Address> addressList = null;

        Geocoder geocoder = new Geocoder(this);
        try {
            addressList = geocoder.getFromLocation(lat, lng, 1);

        } catch (IOException e) {
            e.printStackTrace();
        }

        Address address = addressList.get(0);
        String location = address.getLocality();
        setCity(location);
        createMarker(point);
    }

    //Create a marker on given latlng position
    public void createMarker(LatLng latLng) {
        mMap.clear();

        MarkerOptions marker = new MarkerOptions()
                .position(latLng)
                .title(getCity());

        mMap.addMarker(marker);
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
    }
}