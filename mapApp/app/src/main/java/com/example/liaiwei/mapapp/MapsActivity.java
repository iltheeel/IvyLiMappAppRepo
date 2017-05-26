package com.example.liaiwei.mapapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;




public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback

        {


    private GoogleMap mMap;
    private Fragment frag;
    private SupportMapFragment fm;
    private Integer ch = 0;


            @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        fm = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);
        //frag = (Fragment) findViewById(R.id.map);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

       //adds marker in birthplace
        LatLng markham = new LatLng(44, -79);
        mMap.addMarker(new MarkerOptions().position(markham).title("Born Here"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(markham));


        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            Log.d("self", "perm chekc 1 died");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},2);
        }

        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            Log.d("self", "perm chekc 2 died");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},2);
        }

        mMap.setMyLocationEnabled(true);
        //tries to find self
       // mMap.setOnMyLocationButtonClickListener(this);
       // enableMyLocation();
    }
    public void changer(View v) {
        if(ch%2==0){
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        } else {
            mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        }
        ch++;
    }
        }
