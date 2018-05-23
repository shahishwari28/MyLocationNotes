package com.ishwari.assignment.myapplication;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ishwari.sqlite.helper.MarkerPlace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CurrentLocation extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,OnMapReadyCallback,GoogleMap.OnMapClickListener,GoogleMap.OnMarkerClickListener,LocationListener, ResultCallback<Status>,GoogleMap.OnMarkerDragListener{


    final Context mContext = this;
    Map<String, Circle> circle_hashMap = new HashMap<>();
    DatabaseHelper db;

    private DBManager dbManager;
    FloatingActionButton fab;

    private static final String TAG = CurrentLocation.class.getSimpleName();
    private LocationRequest locationRequest;
    private SupportMapFragment mapFragment;
    private GoogleMap map;
    private GoogleApiClient googleApiClient;
    private Location lastLocation;
    private final int UPDATE_INTERVAL =  3 * 60 * 1000; // 3 minutes
    private final int FASTEST_INTERVAL = 30 * 1000;  // 30 secs
    private Marker locationMarker;
    private static final long GEO_DURATION = 60 * 60 * 1000;
    private static final int DWELLING_TIME=3*60*1000;
    private static final String GEOFENCE_REQ_ID = "My Geofence";
    private static final float GEOFENCE_RADIUS = 500.0f; // in meters
    public static final int REQ_PERMISSION = 99;
    private PendingIntent geoFencePendingIntent;
    private final int GEOFENCE_REQ_CODE = 0;
    private Double Latitude = 0.00;
    private Double Longitude = 0.00;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        dbManager = new DBManager(this);
        dbManager.open();
        createGoogleApi();

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(buttonOnClickListener);
        initGMaps();
    }
    private void createGoogleApi() {
//        Log.d(TAG, "createGoogleApi()");
        if ( googleApiClient == null ) {
            googleApiClient = new GoogleApiClient.Builder( this )
                    .addConnectionCallbacks( this )
                    .addOnConnectionFailedListener( this )
                    .addApi( LocationServices.API )
                    .build();
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        // Call GoogleApiClient connection when starting the Activity
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Disconnect GoogleApiClient when stopping Activity
        googleApiClient.disconnect();
    }
    // Initialize GoogleMaps
    private void initGMaps(){
        mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    // Callback called when Map is ready
    @Override
    public void onMapReady(GoogleMap googleMap) {
//        Log.d(TAG, "onMapReady()");
        map = googleMap;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(CurrentLocation.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {

                map.setMyLocationEnabled(true);
                map.setOnMapClickListener(this);
                map.setOnMarkerDragListener(this);
                map.setOnMarkerClickListener(this);


                float zoom = 14f;
                CameraUpdate cameraUpdate = CameraUpdateFactory.zoomTo(zoom);
                map.animateCamera(cameraUpdate);

            } else {

                checkLocationPermission();
            }
        }
        else {
            createGoogleApi();
            map.setMyLocationEnabled(true);
        }
    }

    // Callback called when Map is touched
    @Override
    public void onMapClick(LatLng latLng) {
//        Log.d(TAG, "onMapClick("+latLng +")");
    }

    // Callback called when Marker is touched
    @Override
    public boolean onMarkerClick(Marker marker) {
//        Log.d(TAG, "onMarkerClickListener: " + marker.getPosition() );
        return false;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
//        Log.i(TAG, "onConnected()");
        getLastKnownLocation();
        startGeofence();
    }

    @Override
    public void onConnectionSuspended(int i) {
//        Log.w(TAG, "onConnectionSuspended()");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//        Log.w(TAG, "onConnectionFailed()");

    }

    @Override
    public void onLocationChanged(Location location) {
        lastLocation = location;
        writeActualLocation(location);
    }
    // Get last known location
    private void getLastKnownLocation() {
//        Log.d(TAG, "getLastKnownLocation()");
        if ( checkPermission() ) {
            lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if ( lastLocation != null ) {
//                Log.i(TAG, "LasKnown location. " +
//                        "Long: " + lastLocation.getLongitude() +
//                        " | Lat: " + lastLocation.getLatitude());
                writeLastLocation();
                startLocationUpdates();
            } else {
//                Log.w(TAG, "No location retrieved yet");
                startLocationUpdates();
            }
        }
        else askPermission();
    }
    private void startLocationUpdates(){
//        Log.i(TAG, "startLocationUpdates()");
        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);

        if ( checkPermission() )
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }
    private void writeActualLocation(Location location) {

        markerLocation(new LatLng(location.getLatitude(), location.getLongitude()));
    }

    private void writeLastLocation() {
        writeActualLocation(lastLocation);
    }
    private boolean checkPermission() {
//        Log.d(TAG, "checkPermission()");
        // Ask for permission if it wasn't granted yet
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED );
    }

    // Asks for permission
    private void askPermission() {
//        Log.d(TAG, "askPermission()");
        ActivityCompat.requestPermissions(
                this,
                new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
                REQ_PERMISSION
        );
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        Log.d(TAG, "onRequestPermissionsResult()");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch ( requestCode ) {
            case REQ_PERMISSION: {
                if ( grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED ){
                    // Permission granted
                    getLastKnownLocation();

                } else {
                    // Permission denied
                    permissionsDenied();
                }
                break;
            }
        }
    }

    // App cannot work without the permissions
    private void permissionsDenied() {
//        Log.w(TAG, "permissionsDenied()");
    }
    private void markerLocation(LatLng latLng) {
//        Log.i(TAG, "markerLocation("+latLng+")");
        String title = latLng.latitude + ", " + latLng.longitude;
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .title(title);
        if ( map!=null ) {
            // Remove the anterior marker
            if ( locationMarker != null )
                locationMarker.remove();
            locationMarker = map.addMarker(markerOptions);
            float zoom = 14f;
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, zoom);
            map.animateCamera(cameraUpdate);
        }
    }

    private Geofence createGeofence(LatLng latLng, float radius,String Id ) {
//        Log.d(TAG, "createGeofence");
        return new Geofence.Builder()
                .setRequestId(Id)
                .setCircularRegion( latLng.latitude, latLng.longitude, radius)
                .setExpirationDuration( GEO_DURATION )
                .setTransitionTypes( Geofence.GEOFENCE_TRANSITION_ENTER)
//                .setLoiteringDelay( DWELLING_TIME)
                .build();
    }
    private GeofencingRequest createGeofenceRequest(Geofence geofence ) {
//        Log.d(TAG, "createGeofenceRequest");
        return new GeofencingRequest.Builder()
                .setInitialTrigger( GeofencingRequest.INITIAL_TRIGGER_ENTER )
                .addGeofence( geofence )
                .build();
    }
    private PendingIntent createGeofencePendingIntent() {
//        Log.d(TAG, "createGeofencePendingIntent");
        if ( geoFencePendingIntent != null )
            return geoFencePendingIntent;

        Intent intent = new Intent( this, GeofenceTrasitionService.class);
        return PendingIntent.getService(
                this, GEOFENCE_REQ_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT );
    }

    // Add the created GeofenceRequest to the device's monitoring list
    private void addGeofence(GeofencingRequest request) {
//        Log.d(TAG, "addGeofence");
        if (checkPermission())
            LocationServices.GeofencingApi.addGeofences(
                    googleApiClient,
                    request,
                    createGeofencePendingIntent()
            ).setResultCallback(this);
    }


    @Override
    public void onResult(@NonNull Status status) {
//        Log.i(TAG, "onResult: " + status);
        if ( status.isSuccess() ) {
            drawGeofence();
        } else {
            // inform about fail
        }
    }

    // Draw Geofence circle on GoogleMap
    private Circle geoFenceLimits;
    private void drawGeofence() {
//        Log.d(TAG, "drawGeofence()");

        if ( geoFenceLimits != null )
            geoFenceLimits.remove();

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    // Start Geofence creation process
    private void startGeofence() {
//        Log.i(TAG, "startGeofence()");
        List<MarkerPlace> markerList  = dbManager.fetchPlaces();
//        System.out.println("----*Marker CNT--"+markerList.size());
        if(markerList.size()>0) {
            MarkerPlace markerPlace;
            for(int i=0;i<markerList.size();i++){
                markerPlace=markerList.get(i);
                Latitude = Double.parseDouble(markerPlace.getLatitude());
                Longitude = Double.parseDouble(markerPlace.getLongitude());
                String name = markerPlace.getPlaceName();
                MarkerOptions marker = new MarkerOptions().position(new LatLng(Latitude, Longitude)).title(name).draggable(true);
                marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                Marker geoMarker = map.addMarker(marker);
                geoMarker.setTag(markerPlace.getPlaceId());

                Geofence geofence = createGeofence(geoMarker.getPosition(), GEOFENCE_RADIUS, geoMarker.getTitle());
                GeofencingRequest geofenceRequest = createGeofenceRequest(geofence);
                addGeofence(geofenceRequest);
                CircleOptions circleOptions = new CircleOptions()
                        .center(geoMarker.getPosition())
                        .strokeColor(Color.argb(50, 70, 70, 70))
                        .fillColor(Color.argb(100, 150, 150, 150))
                        .radius(GEOFENCE_RADIUS);

                Circle geoCircle = map.addCircle( circleOptions );
                circle_hashMap.put((String) geoMarker.getTag(),geoCircle);
            }
        } else {
//            Log.e(TAG, "Geofence marker is null");
        }
    }
//    static Intent makeNotificationIntent(Context geofenceService, String msg)
//    {
////        Log.d(TAG,msg);
//        return new Intent(geofenceService,CurrentLocation.class);
//    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        dbManager.updateLocationLatLong(String.valueOf(marker.getTag()),String.valueOf(marker.getPosition().latitude),String.valueOf(marker.getPosition().longitude));

        //Geo FencingCirlce update

        Circle current_cirlce=circle_hashMap.get(marker.getTag());
        current_cirlce.remove();
        CircleOptions circleOptions = new CircleOptions()
                .center( marker.getPosition())
                .strokeColor(Color.argb(50, 70,70,70))
                .fillColor( Color.argb(100, 150,150,150) )
                .radius( GEOFENCE_RADIUS );
        Circle temp = map.addCircle( circleOptions );
        circle_hashMap.put((String) marker.getTag(),temp);

    }
    public View.OnClickListener buttonOnClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.fab:
                    add_MarkerInfoDialog();
                    break;
            }
        }

    };
    private void add_MarkerInfoDialog() {
        LayoutInflater li = LayoutInflater.from(mContext);
        View dialogView = li.inflate(R.layout.custom_dialog_marker_info, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                mContext);
        alertDialogBuilder.setView(dialogView);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        final EditText place_name = (EditText) dialogView.findViewById(R.id.place_name);
        ((Button)dialogView.findViewById(R.id.cancel_id)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog.cancel();
            }
        });
        ((Button)dialogView.findViewById(R.id.ok_id)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                alertDialog.cancel();
                if(place_name.getText().toString().trim().length()!=0) {
                    createMarker_fun(place_name.getText().toString());
                    alertDialog.cancel();
                } else
                    Toast.makeText(mContext,"Enter name before creating new Location",Toast.LENGTH_LONG).show();
            }
        });

        alertDialog.show();
    }
    private void createMarker_fun(String s) {
        String markerName,latitude,longitude;
        markerName=s;
        LatLng center = map.getCameraPosition().target;
        Projection projection = map.getProjection();
        Point markerPoint = projection.toScreenLocation(center);
        Point targetPoint = new Point(markerPoint.x, markerPoint.y - mapFragment.getView().getHeight() / 6);
        LatLng targetPosition = projection.fromScreenLocation(targetPoint);
        latitude=String.valueOf(targetPosition.latitude);
        longitude=String.valueOf(targetPosition.longitude);
        long id= addingMarkerInDb(markerName,latitude,longitude);

        MarkerOptions tempOpt=new MarkerOptions().title(s)
                .position(targetPosition)
                .draggable(true);
        tempOpt.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
        Marker tempMarker=map.addMarker(tempOpt);
        tempMarker.setTag(String.valueOf(id));
        CircleOptions circleOptions = new CircleOptions()
                .center( targetPosition)
                .strokeColor(Color.argb(50, 70,70,70))
                .fillColor( Color.argb(100, 150,150,150) )
                .radius( GEOFENCE_RADIUS );
        Circle tempCircle = map.addCircle( circleOptions );
        circle_hashMap.put((String) tempMarker.getTag(),tempCircle);

    }
    private long addingMarkerInDb(String name, String latitude, String longitude) {
        long id=dbManager.insertLocation(new MarkerPlace(name,latitude,longitude));
        return id;
    }
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(CurrentLocation.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION );
                            }
                        })
                        .create()
                        .show();


            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION );
            }
        }
    }


}
