package com.mtdev.musicbox.Client.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.mtdev.musicbox.Client.Entities.Shops;
import com.mtdev.musicbox.R;
import com.mtdev.musicbox.application.activities.MainActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by PC on 27/05/2018.
 */

public class GoogleMapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    //  public static final String SERVER_ADDRESS = "http://192.168.1.211:8081/";
    GoogleMap mMap;
    private boolean perGranted;
    private boolean mapReady = false;
    private float zoomLevel = 10;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    private List<Shops> shopsList;

    private static final int MY_LOCATION_REQUEST_CODE = 0;
    private static final int CALL_PHONE_REQUEST_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_googlemap);
        mFusedLocationProviderClient = LocationServices
                .getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        ImageView btn = (ImageView) findViewById(R.id.back_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GoogleMapsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        shopsList = new ArrayList<>();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapReady = true;
        mMap = googleMap;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            mMap.setOnMarkerClickListener(this);
            mMap.getUiSettings().setMapToolbarEnabled(true);
            getDeviceLocation();
            GetLocations(0);

        } else {
            // Show rationale and request permission.
            ActivityCompat.requestPermissions(GoogleMapsActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_LOCATION_REQUEST_CODE);
        }
        //Place Markers

    }

    public void GetLocations(int i) {

        if (mapReady) {
            mMap.clear();
            GetShops();
        }
    }


    private void getDeviceLocation() {
        try {
            Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
            locationResult.addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful()) {

                        // Set the map's camera position to the current location of the device.
                        Location location = task.getResult();
                        if(location!= null){

                            LatLng currentLatLng = new LatLng(location.getLatitude(),
                                    location.getLongitude());

                            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(currentLatLng, zoomLevel);
                            mMap.moveCamera(update);
                        }
                    }
                }
            });
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    public void GetShops() {

        Shops a1 = new Shops("Burger King", 36.8698325, 10.341369100, " Radiohead - creep");
        Shops a2 = new Shops("Salon de thé Kefi", 36.815760, 10.1978601, "Despacito");
        Shops a3 = new Shops("Runis", 36.833129327, 10.30978340, "Eminem - 8 miles ");
        Shops a4 = new Shops("Zafir", 36.8964803, 10.31050099, "Asala - Arabi");
        Shops a5 = new Shops("Hotel mouradi", 36.89186751, 10.197204491, " Kanya west - break her heart ");


        Marker m1 = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(a1.getLat(), a1.getLongi()))
                .snippet("Song On going : " + a1.getAddress())
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_home_location_marker))
                .title(a1.getName()));
        m1.setTag(a1.getNumber());


        Marker m2 = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(a2.getLat(), a2.getLongi()))
                .snippet("Song On going : " + a2.getAddress())
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_home_location_marker))
                .title(a1.getName()));
        m2.setTag(a1.getNumber());

        Marker m3 = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(a3.getLat(), a3.getLongi()))
                .snippet("Song On going : " + a3.getAddress())
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_home_location_marker))
                .title(a3.getName()));
        m3.setTag(a1.getNumber());

        Marker m4 = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(a4.getLat(), a4.getLongi()))
                .snippet("Song On going : " + a4.getAddress())
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_home_location_marker))
                .title(a4.getName()));
        m4.setTag(a1.getNumber());

        Marker m5 = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(a5.getLat(), a5.getLongi()))
                .snippet("Song On going : " + a5.getAddress())
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_home_location_marker))
                .title(a5.getName()));
        m5.setTag(a1.getNumber());

        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(m1.getPosition(), zoomLevel);
        mMap.moveCamera(update);


    }

    private void TextNumber(String numb) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", numb, null)));
    }

    private void CallNumber(String numb) {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.fromParts("tel", numb, null));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(GoogleMapsActivity.this,
                    new String[]{Manifest.permission.CALL_PHONE},
                    CALL_PHONE_REQUEST_CODE);
            return;
        }
        startActivity(intent);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MY_LOCATION_REQUEST_CODE) {
            if (permissions.length == 1 &&
                    permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
                mMap.setOnMarkerClickListener(this);
                mMap.getUiSettings().setMapToolbarEnabled(true);
                getDeviceLocation();
                GetLocations(0);
            } else {
                // Permission was denied. Display an error message.
            }
        }
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        Snackbar mySnackbar = Snackbar.make(findViewById(R.id.container),
                marker.getTitle(), Snackbar.LENGTH_SHORT);
        mySnackbar.setAction("contact", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(GoogleMapsActivity.this);
                builder.setPositiveButton("call", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Call
                        CallNumber("94846836");
                    }
                });
                builder.setNegativeButton("SMS", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Text
                        TextNumber("94846836");
                    }
                });
                builder.setNeutralButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        dialog.dismiss();
                    }
                });
                builder.setTitle("Contacter");
                builder.setMessage("Voulez vous appeler ou envoyer un SMS ?");
                AlertDialog dialog = builder.create();
                /*Button button = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                if (marker.getTag().toString().charAt(0) == '7') {
                    button.setEnabled(true);
                } else
                    button.setEnabled(false);*/
                dialog.show();
            }
        });
        mySnackbar.show();
        return false;
    }
}
