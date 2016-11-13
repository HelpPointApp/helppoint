package com.example.reubert.appcadeirantes.view;

import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.reubert.appcadeirantes.R;
import com.example.reubert.appcadeirantes.manager.GPSManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.reubert.appcadeirantes.model.Help;
import com.example.reubert.appcadeirantes.model.User;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;

import java.util.List;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private GPSManager gpsManager;
    private ParseUser user;
    private List<Help> helps;

    private double _lat;
    private double _long;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        TextView pointsValue = (TextView) findViewById(R.id.myPointsValue);

        try{
            this.user = User.getCurrentUser();
            pointsValue.setText(String.valueOf(this.user.getInt("points")));
        }catch(Exception e){
            Log.e("pointerror", e.toString());
        }
        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        configureGPS();
        configureMap();
        focusOnCurrentUserPosition();
        addHelpMarkers();
    }

    private void configureGPS(){
        gpsManager = GPSManager.getInstance(this);
        gpsManager.enableLocation();
    }

    @SuppressWarnings("MissingPermission")
    private void configureMap(){
        if(GPSManager.isPermissionGranted(this)){
            googleMap.setMyLocationEnabled(true);
        }
    }

    private Help getHelpByLocation(double latitude, double longitude){
        for (int i =0, len = helps.size(); i < len; i++){
            Help help = helps.get(i);
            ParseGeoPoint location = help.getLocation();
            if(location.getLatitude() == latitude && location.getLongitude()==longitude){
                return help;
            }
        }
        return null;
    }

    private void addHelpMarkers(){
        double latitude = this._lat;
        double longitude = this._long;

        try {
            googleMap.clear();
            helps = Help.GetHelpOutOfCloseness(latitude, longitude, 17);

            for (int i = 0, len = helps.size(); i < len; i++) {
                Help help = helps.get(i);
                ParseGeoPoint point = help.getLocation();
                MarkerOptions marker = new MarkerOptions();
                marker.position(new LatLng(point.getLatitude(), point.getLongitude()));
                googleMap.addMarker(marker);
            }
        }catch(ParseException e){
            Log.e("addHelpMarkers", e.toString());
        }

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                LatLng location = marker.getPosition();
                Help help = getHelpByLocation(location.latitude, location.longitude);
                ParseUser userTarget = help.getUserTarget();
                try {
                    userTarget.fetchIfNeeded();

                    // not working, error: java.lang.IllegalStateException: You need to use a Theme.AppCompat theme (or descendant) with this activity
                    AlertDialog.Builder alertBuild = new AlertDialog.Builder(getApplicationContext());
                    alertBuild.setTitle("Ajuda");
                    alertBuild.setMessage("Ajude ao " + userTarget.get("name") + "!");
                    alertBuild.show();
                }catch (Exception e){
                    Log.e("markerclick", e.toString());
                }
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 17));
                return true;
            }
        });
    }

    private void focusOnCurrentUserPosition(){
        Location userLocation = gpsManager.getUserLocation();
        double latitude;
        double longitude;

        if(userLocation != null){
            latitude = userLocation.getLatitude();
            longitude = userLocation.getLongitude();
        } else {
            latitude = -19.9273724;
            longitude = -43.9474144;
        }

        this._lat = latitude;
        this._long = longitude;

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 17));
    }
}
