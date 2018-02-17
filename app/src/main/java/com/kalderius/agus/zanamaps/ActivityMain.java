package com.kalderius.agus.zanamaps;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;

public class ActivityMain extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker marcador;
    private LatLng coord;
    private LocationManager locmanager;
    private Location loca;
    private Escuchador escuchador;
    private GestionDB gestion;
    private Menu menu;
    private List<Marker> lista;
    static final int markermapa=1;

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
        lista = new ArrayList<Marker>();
        gestion = new GestionDB(this);
        this.escuchador = new Escuchador(this);
        while (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ActivityMain.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},PackageManager.PERMISSION_GRANTED);
            try {
                sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        locmanager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        setContentView(R.layout.activity_main);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        loca = locmanager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.monigote:
                //Si pulsa en el monigote se llama a la street view de google Maps
                Uri gmmIntentUri = Uri.parse("google.streetview:cbll="+marcador.getPosition().latitude+","+marcador.getPosition().longitude);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
                return true;
            case R.id.ruta:
                //Si pulsa en calcular ruta se llama a calcular ruta de google MAps
                Uri uri = Uri.parse("google.navigation:q="+marcador.getPosition().latitude+","+marcador.getPosition().longitude);
                Intent mIntent = new Intent(Intent.ACTION_VIEW, uri);
                mIntent.setPackage("com.google.android.apps.maps");
                startActivity(mIntent);
                return true;
            case R.id.MEliminar:
                //Se elimina el marcador
                boolean encontrado = false;
                int i = 0;
                while (!encontrado && i<lista.size()) {
                    Punto pi = (Punto) lista.get(i).getTag();
                    if (((Punto) lista.get(i).getTag()).getCoorx() == ((Punto) marcador.getTag()).getCoorx() &&
                            ((Punto) lista.get(i).getTag()).getCoory() == ((Punto) marcador.getTag()).getCoory()) {
                        marcador.remove();

                        gestion.eliminar(pi);
                        lista.remove(i);
                        encontrado = true;

                    }
                    i++;
                }
                //Se desactiva el menu
                menu.setGroupEnabled(R.id.menu1, false);
                return true;
        }
        return true;

    }

    public GoogleMap getmMap() {
        return mMap;
    }

    public void setmMap(GoogleMap mMap) {
        this.mMap = mMap;
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
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {

                Intent intent=new Intent(ActivityMain.this,ActivityPunto.class);
                Bundle b = new Bundle();
                b.putString("coorx", String.valueOf(latLng.latitude));
                b.putString("coory", String.valueOf(latLng.longitude));
                //añadimos la info al intent
                intent.putExtras(b);
                //Enviamos el intent esperando un resultado de vuelta
                startActivityForResult(intent,markermapa);


                //mMap.addMarker(new MarkerOptions().position(latLng).title("PRUEBA").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                //gestion.insertar(new Punto("prueba",String.valueOf(latLng.latitude),String.valueOf(latLng.longitude),false));
            }
        });
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            //Si el usuario pulsa en un marcador
            public boolean onMarkerClick(Marker marker) {
                menu.setGroupEnabled(R.id.menu1, true);
                //Se activan las opciones de menu
                marcador = marker;
                //Se muestra el nombre del marcador
                marker.showInfoWindow();
                return true;
            }
        });
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                menu.setGroupEnabled(R.id.menu1, false);
            }
        });

        // Comprobamos que se tienen permisos para acceder a la ubicación
        while (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(ActivityMain.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},PackageManager.PERMISSION_GRANTED);
            try {
                sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        //Se pide que se actualice la localización cada 5 segundos
        if(locmanager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            System.out.println("Activado");
        }
        locmanager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 0, 0, escuchador);

        //Se colocan los puntos en el mapa y se activa la ubicación del dispositivo
        colocar(gestion.recuperarPuntos());
        mMap.setMyLocationEnabled(true);
        if (loca == null) loca = new Location(LocationManager.GPS_PROVIDER);
//        coord = new LatLng(loca.getLatitude(), loca.getLongitude());

//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coord,21));

    }


    public List<Marker> getLista() {
        return lista;
    }

    public void setLista(List<Marker> lista) {
        this.lista = lista;
    }

    public LatLng getCoord() {
        return coord;
    }

    public void setCoord(LatLng coord) {
        this.coord = coord;
    }

    /**
     * Método que coloca los puntos que hay en la base de datos en el mapa
     * @param lista Lista con los puntos almacenados
     */
    public void colocar(List lista){
        for (Object o : lista) {
            Punto p = (Punto) o;
            //Se asigna una location con las coordenandas del punto
            Location loc = new Location(LocationManager.GPS_PROVIDER);
            loc.setLatitude(Double.parseDouble(p.getCoorx()));
            loc.setLongitude(Double.parseDouble(p.getCoory()));
            Marker mark = null;
            //Se crea un marcador y se añade a la lista
            if (p.isVisitado()){
                mark = mMap.addMarker(new MarkerOptions().position(new LatLng(loc.getLatitude(), loc.getLongitude())).title(p.getNombre()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
            }
            else{
                 mark = mMap.addMarker(new MarkerOptions().position(new LatLng(loc.getLatitude(), loc.getLongitude())).title(p.getNombre()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            }
            mark.setTag(p);
            this.lista.add(mark);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_en_activity, menu);
        menu.setGroupEnabled(R.id.menu1, false);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == markermapa){

            //Aqui recibimos el resultado del intent de crear una nueva nota
            if(resultCode == RESULT_OK){
                Punto punto = (Punto) data.getExtras().getSerializable("punto");
                LatLng latlong= new LatLng(Double.valueOf(punto.getCoorx()),Double.valueOf(punto.getCoory()));
                lista.add(mMap.addMarker(new MarkerOptions().position(latlong).title(punto.getNombre()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))));
                gestion.insertar(new Punto(punto.getNombre(),String.valueOf(punto.getCoorx()),String.valueOf(punto.getCoory()),false));
            }

        }

    }


}
