package com.kalderius.agus.zanamaps;

import android.app.NotificationManager;
import android.content.Context;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by Agus on 12/02/2018.
 */

public class Escuchador implements LocationListener{
    private ActivityMain activityMain;

    public Escuchador(ActivityMain activityMain) {
        this.activityMain = activityMain;
    }

    @Override
    public void onLocationChanged(Location location) {
        activityMain.setLoca(location);
        comprobar(activityMain.getLoca());
        System.out.println("SETEANDO LOCATION");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public void comprobar(Location loca){
        for (Object o: activityMain.getGestion().recuperarPuntos()
             ) {
            Punto p = (Punto) o;
            Location loc  = new Location(LocationManager.GPS_PROVIDER);
            loc.setLatitude(Double.parseDouble(p.getCoorx()));
            loc.setLongitude(Double.parseDouble(p.getCoory()));
            activityMain.getmMap().addMarker(new MarkerOptions().position(new LatLng(loc.getLatitude(), loc.getLongitude())).title("PRUEBA").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            if (loca.distanceTo(loc) <= 10000){
                NotificationManager notificationManager = (NotificationManager)
                        activityMain.getSystemService(Context.NOTIFICATION_SERVICE);//Se crea el constructor de la notificacion
                NotificationCompat.Builder notificationBuilder;//Se declara la notificacion
                notificationBuilder = new
                        NotificationCompat.Builder(activityMain, "Canal_Notificaciones_1")//Sigue declarando la notificacion
                        .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                        .setContentTitle("Alerta por Frio");
                try{//Evita fallos
                    notificationManager.notify(1, notificationBuilder.build());//Manda la notificacion
                }
                catch (NullPointerException es){
                    es.printStackTrace();
                }
            }
        }
    }
}
