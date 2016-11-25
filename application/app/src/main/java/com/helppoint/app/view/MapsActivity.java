package com.helppoint.app.view;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.helppoint.app.exception.UnknownLocationException;
import com.helppoint.app.wrappers.ProgressDialog;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.helppoint.app.appcadeirantes.R;
import com.helppoint.app.manager.GPSManager;
import com.helppoint.app.model.Help;
import com.helppoint.app.model.User;


import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private enum Status{
        Idle,
        Requesting,
        WaitingHelp,
    }

    private TextView lblMyPoints;
    private Button btnRequestHelp;
    private Intent requestHelpIntent;
    private GoogleMap googleMap;
    private GPSManager gpsManager;

    private ParseUser user;
    private List<Help> helps;
    private Status status;
    private Help helpRequesting;
    private HandleRequestingHelp handleRequestHelp;
    private HandleRequestHelpPosition handleRequestHelpPosition;

    private double _lat;
    private double _long;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        btnRequestHelp = (Button) findViewById(R.id.btnRequestHelp);
        TextView lblMyPoints = (TextView) findViewById(R.id.label_my_points);
        configureAppBar();
        configureTransparencyOnStatusBar();
        loadAllViewElements();

        try{
            this.user = User.getCurrentUser();
            this.user = this.user.fetch();
            int status = this.user.getInt("status");
            User.STATUS currentStatus = User.STATUS.values()[status];
            lblMyPoints.setText(String.valueOf(this.user.getInt("points")));

            if (currentStatus == User.STATUS.HelpInProgress){
                Help.getHelpinProgressByUser(this.user, new FindCallback<Help>() {
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
                        if (e == null){
                            if(objects.size() > 0){
                                Help help = objects.get(0);
                                helpRequesting = help;
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
            refreshMarkers();
        }
    }

    @Override
    public void onMapReady(GoogleMap _googleMap) {
        this.googleMap = _googleMap;

        configureGPS();
        configureMap();
        focusOnCurrentUserPosition();
        refreshMarkers();
        setGoogleMapEvents();
    }

    private void setGoogleMapEvents(){
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                LatLng location = marker.getPosition();
                Help help = getHelpByMarkerLocation(location.latitude, location.longitude);
                startActivityHelp(help.getObjectId(), location);
                return true;
            }
        });

        googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                refreshMarkers();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            try {
                if (resultCode == Activity.RESULT_OK) {
                    helpRequesting = Help.getActive();
                    startHandleRequestHelp();
                    this.onChangeStatus(Status.Requesting);
                }
            } catch (Exception e) {
                Log.e("error activity result", e.toString());
            }
        }else if (requestCode == 2){
            try{
                if (resultCode == Activity.RESULT_OK){
                    Intent intent = new Intent(MapsActivity.this, RatingActivity.class);
                    startActivity(intent);
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
        stopHandleRequestHelp();
        stopHandleRequestPosition();
    }

    @Override
    public void onStop(){
        super.onStop();
        stopHandleRequestHelp();
        stopHandleRequestPosition();
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

    private void refreshMarkers(){
        double latitude = this._lat;
        double longitude = this._long;
        int zoom = (int) googleMap.getCameraPosition().zoom;

        if (status == Status.Idle) {
            googleMap.clear();
            requestNearHelpers(latitude, longitude, zoom);
        }else if(status == Status.Requesting){
            googleMap.clear();
            requestNearUsers(latitude, longitude);
        }
    }

    private void requestNearUsers(double latitude, double longitude){
        User.getNearUsers(latitude, longitude, new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, ParseException possibleException) {
                if(possibleException != null){
                    Log.d("Exception", "Couldn't get near users.");
                    return;
                }

                ParseGeoPoint point;
                MarkerOptions markerOptions;

                for(ParseUser user: users){
                    point = (ParseGeoPoint) user.get("lastPosition");
                    markerOptions = (new MarkerOptions()).position(new LatLng(point.getLatitude(), point.getLongitude()));
                    googleMap.addMarker(markerOptions);
                }
            }
        });
    }

    private void requestNearHelpers(double latitude, final double longitude, int zoom){
        Help.getHelpOutOfCloseness(latitude, longitude, zoom, new FindCallback<Help>() {
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
                        marker.snippet("" + point.getLatitude() + "," + point.getLongitude());
                        googleMap.addMarker(marker);
                    }
                }
            }
        });
    }

    private void focusOnCurrentUserPosition(){
        try {
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
                    CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 17)
            );
        } catch(UnknownLocationException e){
            //@toDo: implement requestSingleUpdate after
        }
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
        else if (status == Status.Requesting){
            final ProgressDialog progressDialog = ProgressDialog.getInstance();
            btnRequestHelp.setText("CANCELAR");
            btnRequestHelp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    progressDialog.create(MapsActivity.this, "Cancelado", "Aguarde enquanto estamos cancelando seu pedido");
                    stopHandleRequestHelp();
                    helpRequesting.cancel(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            progressDialog.hide();
                            onChangeStatus(Status.Idle);
                            refreshMarkers();
                        }
                    });
                }
            });
        }else if (status == Status.WaitingHelp){
            btnRequestHelp.setText("CHEGOU!");
            btnRequestHelp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    stopHandleRequestPosition();
                    startActivityHelp(helpRequesting.getObjectId(), null); // mudar isso porque eh pra aparecer outra tela
                }
            });
        }
        this.status = status;
    }

    private void startActivityHelp(String objectId, LatLng location){
        Intent helpActivity = new Intent(MapsActivity.this, HelpActivity.class);
        helpActivity.putExtra("objectId", objectId);
        helpActivity.putExtra("helpLocationLatitude", location.latitude);
        helpActivity.putExtra("helpLocationLongitude", location.longitude);
        startActivityForResult(helpActivity, 2);
    }

    private void waitUserNear(Help help){
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(new LatLng(0, 0));
        Marker marker = googleMap.addMarker(markerOptions);
        startHandleRequestPosition(help, marker);
        onChangeStatus(Status.WaitingHelp);
    }

    private void startHandleRequestHelp(){
        if(handleRequestHelp != null && handleRequestHelp.isAlive()){
            handleRequestHelp.interrupt();
        }

        handleRequestHelp = new HandleRequestingHelp();
        handleRequestHelp.start();
    }

    private void stopHandleRequestHelp(){
        if (handleRequestHelp != null && handleRequestHelp.isAlive()){
            handleRequestHelp.isInterrupted();
        }
    }

    private void startHandleRequestPosition(Help help, Marker marker){
        if(handleRequestHelpPosition != null && handleRequestHelpPosition.isAlive()){
            handleRequestHelpPosition.interrupt();
        }

        handleRequestHelpPosition = new HandleRequestHelpPosition(help, marker);
        handleRequestHelpPosition.start();
    }

    private void stopHandleRequestPosition(){
        if (handleRequestHelpPosition != null && handleRequestHelpPosition.isAlive()){
            handleRequestHelpPosition.isInterrupted();
        }
    }

    private class HandleRequestingHelp extends Thread{
        @Override
        public void run(){
            // final String objectId = helpRequesting.getObjectId();
            boolean running = true;
            try {
                Help help = Help.getByObjectId(helpRequesting.getObjectId());
                while(running){
                    help.fetch();
                    Help.STATUS status = help.getStatus();

                    if (status == Help.STATUS.Helping){
                        running = false;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                waitUserNear(helpRequesting);
                            }
                        });
                    }

                    Thread.sleep(1000);
                }
            }catch(Exception e){
                Log.e("error request helo", e.toString());
            }
        }
    }

    private class HandleRequestHelpPosition extends Thread {

        private Help help;
        private Marker marker;

        public HandleRequestHelpPosition(Help help, Marker marker){
            super();
            this.help = help;
            this.marker = marker;
        }

        @Override
        public void run(){
            ParseUser user = help.getHelpedParseUser();
            ParseUser userHelp;

            try{
                while(true){
                    userHelp = user.fetch();
                    final ParseGeoPoint geoPoint = (ParseGeoPoint) userHelp.get("lastPosition");
                    if (geoPoint != null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                marker.setPosition(
                                        new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude()));
                            }
                        });
                    }
                    Thread.sleep(2000);
                }
            }catch(Exception e){
                Log.e("error request position", e.toString());
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    marker.remove();
                }
            });
        }
    }
}
