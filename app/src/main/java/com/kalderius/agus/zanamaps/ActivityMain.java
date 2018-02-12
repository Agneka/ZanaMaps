package com.kalderius.agus.zanamaps;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.sql.SQLOutput;

import static java.lang.Thread.sleep;

public class ActivityMain extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private MarkerOptions posicionActual;
    private LatLng coord;
    private LocationManager locmanager;
    private Location loca;
    private Escuchador escuchador;
    private GestionDB gestion;

    public GestionDB getGestion() {
        return gestion;
    }

    public void setGestion(GestionDB gestion) {
        this.gestion = gestion;
    }

    public Location getLoca() {
        return loca;
    }

    public void setLoca(Location loca) {
        this.loca = loca;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gestion = new GestionDB(this);
        this.escuchador = new Escuchador(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(ActivityMain.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},PackageManager.PERMISSION_GRANTED);

        }
        locmanager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        setContentView(R.layout.activity_main);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        posicionActual = new MarkerOptions();

        loca = locmanager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
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
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mMap.addMarker(new MarkerOptions().position(latLng).title("PRUEBA").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                gestion.insertar(new Punto("prueba",String.valueOf(latLng.latitude),String.valueOf(latLng.longitude),false));
            }
        });
        // Add a marker in Sydney and move the camera
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(ActivityMain.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},PackageManager.PERMISSION_GRANTED);

        }

        locmanager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 0, 0, escuchador);
        try {
            sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(loca.getLatitude());
        coord = new LatLng(loca.getLatitude(), loca.getLongitude());
        System.out.println("LONGITUD: "+loca.getLongitude());
        mMap.setMyLocationEnabled(true);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coord,50));

    }


}
