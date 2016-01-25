package com.yuxuanli.weatherforecastinandroid;


import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.content.Intent;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.hamweather.aeris.communication.AerisEngine;
import com.hamweather.aeris.maps.AerisMapView;
import com.hamweather.aeris.maps.MapViewFragment;
import com.hamweather.aeris.tiles.AerisTile;

import org.w3c.dom.Text;
import android.util.Log;


/**
 * Created by LiYuxuan on 11/22/15.
 */
public class MapActivity extends Activity {

    static Double lon = 0.0;
    static Double lat = 0.0;
    private static final String TAG = "MyActivity";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_container);

        Log.v(TAG, "MapActivity start");


        Intent i = getIntent();

        lon = i.getDoubleExtra("lon",0);
        lat = i.getDoubleExtra("lat",0);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Log.v(TAG,"beginTransaction");

        MapFragment fragment = new MapFragment();
        Log.v(TAG,"create the MapFragment");
        fragmentTransaction.add(R.id.map_container, fragment);
        Log.v(TAG,"add fragment to container");
        fragmentTransaction.commit();
        Log.v(TAG,"commit");
    }

    public static class MapFragment extends MapViewFragment {
        private static final String TAG = "MyActivity";
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            View view = inflater.inflate(R.layout.map, container, false);

            AerisEngine.initWithKeys(getResources().getString(R.string.aeris_client_id), getResources().getString(R.string.aeris_client_secret), "com.yuxuanli.weatherforecastinandroid");
            Log.v(TAG, "init engine");
            mapView = (AerisMapView) view.findViewById(R.id.aerisfragment_map);
            mapView.init(savedInstanceState, AerisMapView.AerisMapType.GOOGLE);
            Log.v(TAG,"init map");

            LatLng loc = new LatLng(lat, lon);
            mapView.moveToLocation(loc,11);
            mapView.addLayer(AerisTile.RADAR);

            Log.v(TAG,"already addlayer");

            return view;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }


}










