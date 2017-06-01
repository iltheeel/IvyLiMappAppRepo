package com.example.liaiwei.mapapp;

import android.graphics.Color;
import android.graphics.drawable.shapes.RectShape;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback

{
    private static final long MIN_TIME_BW_UPDATES = 15000;
    private static final long MIN_DIST_CHANGE_FOR_UPDATES = 5;
    private static final int MY_LOC_ZOOM_FACTOR = 15;
    private GoogleMap mMap;
    private LocationManager locationManager;
    private Integer ch = 0;
    private boolean isNetworkenabled = false;
    private boolean isGPSenabled = false;
    private boolean canGetLocation = false;
    private Location location;
    private Location myLocation;
    private LatLng userLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        // fm = (SupportMapFragment)
        //  getSupportFragmentManager().findFragmentById(R.id.map);
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


        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("self", "perm chekc 1 died");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);
        }

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("self", "perm chekc 2 died");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
        }

        mMap.setMyLocationEnabled(true);
        //tries to find self
        // mMap.setOnMyLocationButtonClickListener(this);
        // enableMyLocation();
    }

    public void getLocation(View v) {
        try {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            //get gps status
            isGPSenabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (isGPSenabled) {
                Log.d("self", "getLocation: GPS is enabled");
            }

            //get network status
            isNetworkenabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (isNetworkenabled) {
                Log.d("self", "getLocation: network is enabled");
            }

            if (!isNetworkenabled && !isGPSenabled) {
                Log.d("self", "getLocation: no provider enabled");
            } else {
                this.canGetLocation = true;
                if (isNetworkenabled) {
                    Log.d("self", "getlocation network enabled - requesting lcoation updates");
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DIST_CHANGE_FOR_UPDATES,
                            locationListnerNetwork);
                    Log.d("self", "getLocation network network getlocation upzte");
                    Toast.makeText(this, "using network", Toast.LENGTH_SHORT).show();
                }
                if (isGPSenabled) {
                    Log.d("self", "getlocation network enabled - requesting lcoation updates");
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DIST_CHANGE_FOR_UPDATES,
                            locationListnerGPS);
                    Log.d("self", "getLocatio gps is getting updates");
                    Toast.makeText(this, "using gps", Toast.LENGTH_SHORT).show();
                }
            }

        } catch (Exception e) {
            Log.d("self", "caught exception in getlocation");
            e.printStackTrace();
        }
    }

    android.location.LocationListener locationListnerNetwork = new android.location.LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            //output logd for network running
            Log.d("self", "network is running");
            //drop marker
            dropmarker("hi");

            //relaunch network provider (requestLocationUpdates (NETWORK_PROVIDER))
            try {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DIST_CHANGE_FOR_UPDATES,
                        locationListnerNetwork);

            } catch (SecurityException e) {
                Log.d("self", "onstatuschangednetwork security exception 2");
            }

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            //output logd and stoast
            switch (status) {
                case LocationProvider.AVAILABLE:
                    Log.d("self", "location provider in onstatuschanged FOR NETWORKING is available");
                    break;
                case LocationProvider.OUT_OF_SERVICE:
                    Log.d("self", "location provider network out of service");
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Log.d("self", "location provider network out of service");
                    break;
                default:
                    Log.d("self", "location provider network out of service");
                    break;
            }
        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };



    android.location.LocationListener locationListnerGPS = new android.location.LocationListener() {
        @Override
        public void onLocationChanged(Location loc) {
            Log.d("self", "gps is enabled in onLocationChanged");
            dropmarker("crap");
            isNetworkenabled = false;
            //create a method for dropping a marker
            //location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            //remove network location updates. see locationmanager for update removal
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.d("self", "gps is enabled in onStatusChanged");

            switch (status) {
                case LocationProvider.AVAILABLE:
                    Log.d("self", "location provider in onstatuschanged is available FOR GPS");

                    break;
                case LocationProvider.OUT_OF_SERVICE:
                    try {
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DIST_CHANGE_FOR_UPDATES,
                                locationListnerNetwork);

                    } catch (SecurityException e) {
                        Log.d("self", "onstatuschangedgps security exception 2");
                    }
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    try {
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DIST_CHANGE_FOR_UPDATES,
                                locationListnerNetwork);

                    } catch (SecurityException e) {
                        Log.d("self", "onstatuschangedgps security exception 2");
                    }
                    break;
                default:
                    try {
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DIST_CHANGE_FOR_UPDATES,
                                locationListnerNetwork);

                    } catch (SecurityException e) {
                        Log.d("self", "onstatuschangedgps security exception 2");
                    }
                    break;
            }


            //setup switch statement to check status input parameter
            //case LocationProvider.AVAILABLE --> output to logd and toast
            //case LocationProvider.OUT_OF_SERVICE --> request update from network
            //case LocationProvider.TEMPORARILY_UNAVAILABLE -->request from NETWORK_PROVIDER
            //case default --> request from provider


        }

        @Override
        public void onProviderEnabled(String provider) {
//dont need
        }

        @Override
        public void onProviderDisabled(String provider) {
//dont need
        }

    };



    //public void dropMarker(double lat, double log) {
    public void dropmarker(String provider) {

        userLocation = new LatLng(0,0);
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            myLocation = locationManager.getLastKnownLocation(provider);

        }

        if(myLocation == null) {
            Toast.makeText(this, "null myLocation", Toast.LENGTH_SHORT).show();
            Log.d("self", "null myloca");

        } else {

            userLocation = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
            //display mssg with lat and long
            String dead = "YOU ARE AT " + myLocation.getLatitude() + " " + myLocation.getLongitude();
            Toast.makeText(this, dead, Toast.LENGTH_SHORT).show();

            //camera update
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(userLocation, MY_LOC_ZOOM_FACTOR);


            //drop the beat marker
             Circle circle = mMap.addCircle(new CircleOptions()
                     .center(userLocation)
                     .radius(1)
                     .strokeColor(Color.RED)
                     .strokeWidth(2)
                     .fillColor(Color.BLACK));
            mMap.animateCamera(update);

        }


        //LatLng drop = new LatLng(lat, log);
        //Marker marker = mMap.addMarker(new MarkerOptions().position(drop).title("your location"));
                //.icon(BitmapDescriptorFactory.fromResource(R.drawable.arrow)));
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