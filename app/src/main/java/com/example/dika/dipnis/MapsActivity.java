package com.example.dika.dipnis;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.Address;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMapLongClickListener {

    public static final int REQUEST_LOCATION = 1;

    GoogleMap myMap;
    GoogleApiClient myGoogleApiClient;
    Location myLastLocation;
    Marker myLocationMarker;
    LocationRequest myLocationRequest;

    String markerPosition;
    boolean setMarker, zoomMyLocation;
    Button btnPostavi;
    AlertDialog.Builder adb;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        setMarker = true;
        zoomMyLocation = true;
        btnPostavi = (Button) findViewById(R.id.mapaBtnPostavi);
        btnPostavi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        myMap = googleMap;
        myMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        myMap.setOnMapLongClickListener(this);

        //Inicijalizacija Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //za SDK veci od 23
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                myMap.setMyLocationEnabled(true);
                myMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                checkLocationPermission();
            }
        }
        else {
            //za SDK manji od 23
            buildGoogleApiClient();
            myMap.setMyLocationEnabled(true);
            myMap.getUiSettings().setMyLocationButtonEnabled(true);
        }

        Intent intent = getIntent();
        markerPosition = intent.getStringExtra("markerPosition");
        if (!markerPosition.equals("noPosition")) {
            setMarker = false;
            zoomMyLocation = false;
            Geocoder geocoder = new Geocoder(this);
            List<Address> addressList = null;
            try {
                addressList = geocoder.getFromLocationName(markerPosition, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Address address = addressList.get(0);
            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
            myLocationMarker = myMap.addMarker(new MarkerOptions().position(latLng).title(markerPosition));
            myLocationMarker.showInfoWindow();
            myMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            myMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        }
    }

    //kreiranje Google Api Client-a
    protected synchronized void buildGoogleApiClient() {
        myGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        myGoogleApiClient.connect();
    }

    //konektovanje Google Api Client-a
    @Override
    public void onConnected(Bundle bundle) {
        myLocationRequest = new LocationRequest();
        myLocationRequest.setInterval(2000);
        myLocationRequest.setFastestInterval(2000);
        myLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(myGoogleApiClient, myLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {}

    //postavljanje trenutne lokacije (na svaku promenu lokacije)
    @Override
    public void onLocationChanged(Location location) {
        myLastLocation = location;

        //Postavka markera na trenutnu poziciju
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        //Pomeraj kamere i zumiranje na trenutnu lokaciju
        if (zoomMyLocation) {
            myMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            myMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        }

        //prekid update-ovanja lokacije
        if (myGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(myGoogleApiClient, this);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {}

    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        if (myGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        myMap.setMyLocationEnabled(true);
                    }
                } else {
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        if (setMarker) {
            Geocoder geocoder = new Geocoder(this);
            List<Address> addressList = null;
            try {
                addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 2);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Address a1 = addressList.get(0);
            String adr1[] = a1.toString().split("\"")[1].split(", ");
            String address1 = "";
            for (int i = 0; i < adr1.length - 2; i++) {
                address1 += (i == 0 ? "" : ", ") + adr1[i];
            }
            Address a2 = addressList.get(1);
            String adr2[] = a2.toString().split("\"")[1].split(", ");
            String address2 = "";
            for (int i = 0; i < adr2.length - 1; i++) {
                address2 += (i == 0 ? "" : ", ") + adr2[i];
            }
            final LatLng latLon = latLng;
            final String[] adbItems = {address1, address2};
            adb = new AlertDialog.Builder(MapsActivity.this);
            adb.setTitle(getResources().getString(R.string.strAdbTitleNazivLokacije));
            adb.setItems(adbItems, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int i) {
                    if (myLocationMarker != null)
                        myLocationMarker.remove();
                    myLocationMarker = myMap.addMarker(new MarkerOptions().position(latLon).title(adbItems[i]));
                    myLocationMarker.showInfoWindow();
                    zoomMyLocation = false;
                    btnPostavi.setVisibility(View.VISIBLE);
                }
            });
            adb.setIcon(R.drawable.location_icon_red);
            adb.show();
        }
    }
}
