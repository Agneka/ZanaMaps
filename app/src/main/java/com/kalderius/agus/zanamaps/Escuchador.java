package com.kalderius.agus.zanamaps;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.sql.SQLOutput;

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

            if (loca.distanceTo(loc) > 30 && loca.distanceTo(loc) <= 3000 && !p.isVisitado()){
                NotificationManager notificationManager = (NotificationManager)
                        activityMain.getSystemService(Context.NOTIFICATION_SERVICE);//Se crea el constructor de la notificacion
                NotificationCompat.Builder notificationBuilder;//Se declara la notificacion
                notificationBuilder = new
                        NotificationCompat.Builder(activityMain, "Canal_Notificaciones_1")//Sigue declarando la notificacion
                        .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                        .setContentTitle("Alerta por Frio")
                        .setVibrate(new long[]{1, 1});
                try{//Evita fallos
                    notificationManager.notify(1, notificationBuilder.build());//Manda la notificacion
                }
                catch (NullPointerException es){
                    es.printStackTrace();
                }
            }
            if (loca.distanceTo(loc) <= 3 && !p.isVisitado()){
                for (int i = 0; i < activityMain.getLista().size(); i++) {
                    System.out.println("buscando al afortunado");
                    if (activityMain.getLista().get(i).getPosition().longitude == loc.getLongitude() && activityMain.getLista().get(i).getPosition().latitude == loc.getLatitude()){
                        activityMain.getLista().get(i).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
                        activityMain.getGestion().marcarVisto(p);
                    }
                }
            }
        }
    }
}
