package com.example.reubert.appcadeirantes.view;

import android.content.Intent;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

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
        configureAppBar();


        TextView pointsValue = (TextView) findViewById(R.id.lblPoints);

        try{
            this.user = User.getCurrentUser();
            this.user.fetchIfNeeded();
            int status = this.user.getInt("status");
            pointsValue.setText(String.valueOf(this.user.getInt("points")));

            if (User.STATUS.values()[status] != User.STATUS.Hidden){
                Help.getHelpByUserHelper(this.user, new FindCallback<Help>() {
                    @Override
                    public void done(List<Help> objects, ParseException e) {

                        if(e == null){
                            if (objects.size() > 0){
                                Help help = objects.get(0);
                                Intent helpActivity = new Intent(MapsActivity.this, HelpActivity.class);
                                helpActivity.putExtra("objectId", help.getObjectId());
                                startActivity(helpActivity);
                            }
                        }
                        else{
                            Log.e("statuserror", e.toString());
                        }
                    }
                });
            }
        }catch(Exception e){
            Log.e("pointerror", e.toString());
        }

        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Button btnRequestHelp = (Button) findViewById(R.id.btnRequestHelp);
        btnRequestHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent helpActivity = new Intent(MapsActivity.this, RequestHelpActivity.class);
                startActivity(helpActivity);
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();

        if(this.googleMap != null) {
            updateHelpMarkers();
        }
    }

    @Override
    public void onMapReady(GoogleMap _googleMap) {
        this.googleMap = _googleMap;

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                LatLng location = marker.getPosition();
                Help help = getHelpByMarkerLocation(location.latitude, location.longitude);
                ParseUser userTarget = help.getUserTarget();
                try {
                    userTarget.fetchIfNeeded();
                    Intent helpActivity = new Intent(MapsActivity.this, HelpActivity.class);
                    helpActivity.putExtra("objectId", help.getObjectId());
                    startActivity(helpActivity);
                }catch (Exception e) {
                    Log.e("markerclick", e.toString());
                }
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 17));
                return true;
            }
        });

        configureGPS();
        configureMap();
        focusOnCurrentUserPosition();
        updateHelpMarkers();
    }

    private void configureAppBar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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

    private Help getHelpByMarkerLocation(double latitude, double longitude){
        for (int i =0, len = helps.size(); i < len; i++){
            Help help = helps.get(i);
            ParseGeoPoint location = help.getLocation();
            if(location.getLatitude() == latitude && location.getLongitude()==longitude){
                return help;
            }
        }
        return null;
    }

    private void updateHelpMarkers(){
        double latitude = this._lat;
        double longitude = this._long;
        int zoom = (int) googleMap.getCameraPosition().zoom;

        googleMap.clear();
        Help.GetHelpOutOfCloseness(latitude, longitude, zoom, new FindCallback<Help>() {
            @Override
            public void done(List<Help> objects, ParseException e) {
                helps = objects;
                for (int i = 0, len = objects.size(); i < len; i++) {
                    Help help = objects.get(i);
                    ParseGeoPoint point = help.getLocation();
                    MarkerOptions marker = new MarkerOptions();
                    marker.position(new LatLng(point.getLatitude(), point.getLongitude()));
                    googleMap.addMarker(marker);
                }
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
