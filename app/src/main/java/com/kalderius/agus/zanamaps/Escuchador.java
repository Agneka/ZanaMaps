package com.kalderius.agus.zanamaps;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Agus on 12/02/2018.
 */

public class Escuchador implements LocationListener{
    private ActivityMain activityMain;
    private Toast toast;
    private List<Punto> lista;

    public Escuchador(ActivityMain activityMain) {
        this.activityMain = activityMain;
        toast = new Toast(activityMain);
        lista = new ArrayList();

    }

    @Override
    public void onLocationChanged(Location location) {
//        toast.cancel();
//        toast = Toast.makeText(activityMain, "SETEANDO LOCATION", Toast.LENGTH_SHORT);
//        toast.show();
//        System.out.println("SETEANDO LOCATION");
        activityMain.setLoca(location);
        activityMain.getmMap().moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 21));
        comprobar(activityMain.getLoca());

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
                Uri uri = Uri.parse("google.navigation:q="+loc.getLatitude()+","+loc.getLongitude());
                Intent mIntent = new Intent(Intent.ACTION_VIEW, uri);
                mIntent.setPackage("com.google.android.apps.maps");
                NotificationManager notificationManager = (NotificationManager)
                        activityMain.getSystemService(Context.NOTIFICATION_SERVICE);//Se crea el constructor de la notificacion
                NotificationCompat.Builder notificationBuilder;//Se declara la notificacion
                notificationBuilder = new
                        NotificationCompat.Builder(activityMain, "Canal_Notificaciones_1")//Sigue declarando la notificacion
                        .setSmallIcon(R.drawable.marcador)
                        .setContentIntent(PendingIntent.getActivity(activityMain,0, mIntent, 0))
                        .setContentTitle("Tu destino "+p.getNombre()+" está cerca.")
                        .setContentText("Pulsa en esta notificación para calcular la ruta a "+p.getNombre())
                        .setVibrate(new long[]{1, 1})
                        .setAutoCancel(true)
                        .setOnlyAlertOnce(true)
                ;
                boolean contiene = false;
                //Se comprueba que esa notificación no se ha lanzado.
                for (Punto pa :
                        lista) {
                    if (p.getNombre().equalsIgnoreCase(pa.getNombre())){
                        contiene = true;
                    }
                }
                if(!contiene){
                    lista.add(p);
                    notificationManager.notify(lista.size(), notificationBuilder.build());
                }
                try{//Evita fallos

                    //Manda la notificacion
                }
                catch (NullPointerException es){
                    es.printStackTrace();
                }
            }
            //Si la distancia es menor a 3 metros y no ha sido visitado
            if (loca.distanceTo(loc) <= 3 && !p.isVisitado()){
                for (int i = 0; i < activityMain.getLista().size(); i++) {
                    //Se busca el marcador para cambiarle el color
//                    System.out.println("buscando al afortunado");
                    if (activityMain.getLista().get(i).getPosition().longitude == loc.getLongitude() && activityMain.getLista().get(i).getPosition().latitude == loc.getLatitude()){
//                        toast.cancel();
//                        toast = Toast.makeText(activityMain, "PUNTO ENCONTRADO", Toast.LENGTH_SHORT);
//                        toast.show();
                        //Se cambia el color y se marca como visto

                        activityMain.getLista().get(i).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
                        activityMain.getGestion().marcarVisto(p);
                    }
                }
            }
        }
    }
}
