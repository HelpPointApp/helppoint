package com.example.reubert.appcadeirantes.view;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.media.tv.TvContract;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.reubert.appcadeirantes.model.Help;
import com.example.reubert.appcadeirantes.model.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private TextView lblMyPoints;

    private enum Status{
        Idle,
        Requesting,
    }

    private GoogleMap googleMap;
    private GPSManager gpsManager;
    private ParseUser user;
    private List<Help> helps;
    private Intent requestHelpIntent;
    private Status status;
    private Help helpRequesting;
    private Button btnRequestHelp;
    private HandleRequestingHelp handleRequestHelp;

    private double _lat;
    private double _long;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        btnRequestHelp = (Button) findViewById(R.id.btnRequestHelp);
        TextView lblMyPoints = (TextView) findViewById(R.id.lblPoints);

        configureAppBar();
        configureTransparencyOnStatusBar();
        loadAllViewElements();

        try{
            this.user = User.getCurrentUser();
            this.user.fetchIfNeeded();
            int status = this.user.getInt("status");
            User.STATUS currentStatus = User.STATUS.values()[status];
            lblMyPoints.setText(String.valueOf(this.user.getInt("points")));


            if (currentStatus == User.STATUS.HelpInProgress){
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
            }else if (currentStatus == User.STATUS.RequestingHelp){
                Help.getRequestHelpByUser(user, new FindCallback<Help>() {
                    @Override
                    public void done(List<Help> objects, ParseException e) {
                        if (e != null){
                            if(objects.size() > 0){
                                Help _help = objects.get(0);
                                helpRequesting = _help;
                                onChangeStatus(Status.Requesting);
                            }
                        }
                    }
                });
            }
        }catch(Exception e){
            Log.e("pointerror", e.toString());
        }

        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        if (status == null)
            this.onChangeStatus(Status.Idle);
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

        configureGPS();
        configureMap();
        focusOnCurrentUserPosition();
        updateHelpMarkers();
        setGoogleMapEvents();
    }

    private void setGoogleMapEvents(){
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                LatLng location = marker.getPosition();
                Help help = getHelpByMarkerLocation(location.latitude, location.longitude);
/*                if (!help.getUserTarget().getObjectId().equals(user.getObjectId()))*/
                startActivityHelp(help.getObjectId());
//                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 17));
                return true;
            }
        });

        googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                updateHelpMarkers();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    Help help = Help.getHelp(data.getExtras().getString("objectId"));
                    helpRequesting = help;
                    handleRequestHelp = new HandleRequestingHelp();
                    handleRequestHelp.start();
                    this.onChangeStatus(Status.Requesting);
                } catch (Exception e) {
                    Log.e("error activity result", e.toString());
                }
            }
        }else if (requestCode == 2){
            try{
                if (resultCode == Activity.RESULT_OK){
                    helpRequesting = Help.getHelp(data.getExtras().getString("objectId"));
                    this.onChangeStatus(Status.Idle);
                }
            }catch(Exception e){
                Log.e("error result", e.toString());
            }
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        if(handleRequestHelp != null && handleRequestHelp.isAlive()){
            handleRequestHelp.isInterrupted();
        }
    }

    @Override
    public void onStop(){
        super.onStop();
        if (handleRequestHelp != null && handleRequestHelp.isAlive()){
            handleRequestHelp.isInterrupted();
        }
    }

    private void configureAppBar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void configureTransparencyOnStatusBar(){
        //LayoutManager.enableTransparentStatusBar(this, (Toolbar) findViewById(R.id.toolbar));
    }

    private void loadAllViewElements(){
        lblMyPoints = (TextView) findViewById(R.id.label_my_points);
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
                if (e == null && objects != null) {
                    for (int i = 0, len = objects.size(); i < len; i++) {
                        Help help = objects.get(i);
                        ParseGeoPoint point = help.getLocation();
                        MarkerOptions marker = new MarkerOptions();
                        marker.position(new LatLng(point.getLatitude(), point.getLongitude()));
                        marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker));
                        googleMap.addMarker(marker);
                    }
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

        googleMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 17));
    }

    private void onChangeStatus(Status status){
        if (status == Status.Idle){
            btnRequestHelp.setText("PEDIR AJUDA");
            btnRequestHelp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    requestHelpIntent = new Intent(MapsActivity.this, RequestHelpActivity.class);
                    startActivityForResult(requestHelpIntent, 1);
                }
            });
        }
        if (status == Status.Requesting){
            final Context context = this;
            btnRequestHelp.setText("CANCELAR");
            btnRequestHelp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final ProgressDialog progressDialog = new ProgressDialog(context);
                    progressDialog.setTitle("Cancelando");
                    progressDialog.setMessage("Aguarde enquanto estamos cancelando seu pedido");
                    progressDialog.show();
                    handleRequestHelp.interrupt();
                    handleRequestHelp = null;
                    helpRequesting.cancel(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            progressDialog.dismiss();
                            onChangeStatus(Status.Idle);
                            updateHelpMarkers();
                        }
                    });
                    if (handleRequestHelp != null){
                        handleRequestHelp.interrupt();
                    }
                }
            });
        }
        this.status = status;
    }

    private void startActivityHelp(String objectId){
        Intent helpActivity = new Intent(MapsActivity.this, HelpActivity.class);
        helpActivity.putExtra("objectId", objectId);
        startActivityForResult(helpActivity, 2);
    }

    private class HandleRequestingHelp extends Thread{
        @Override
        public void run(){
            // final String objectId = helpRequesting.getObjectId();
            boolean running = true;
            while(running){
                try {
                    Help help = Help.getHelp(helpRequesting.getObjectId());
                    Help.STATUS status = help.getStatus();

                    if (status == Help.STATUS.Helping){
                        running = false;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                startActivityHelp(helpRequesting.getObjectId());
                            }
                        });
                    }

                    Thread.sleep(1000);
                }catch(Exception e){
                    Log.e("error request helo", e.toString());
                }
            }
        }
    }
}
