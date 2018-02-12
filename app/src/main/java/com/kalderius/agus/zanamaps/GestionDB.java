package com.kalderius.agus.zanamaps;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by david on 12/02/2018.
 */

public class GestionDB {
    private SQLiteDatabase db;

    public GestionDB(ActivityMain act) {
        this.db=act.openOrCreateDatabase("PuntosDB",act.MODE_PRIVATE,null);
        crearTabla();
    }
    public void crearTabla(){
        String crear="CREATE TABLE IF NOT EXISTS Puntos(id INTEGER PRIMARY KEY, nombre VARCHAR(100),coorX VARCHAR(250), coorY VARCHAR(250), visitado INTEGER );";
        db.execSQL(crear);

    }
    public void insertar(Punto punto){
        String visitado;
        if (punto.isVisitado()){
            visitado="1";
        }else {
            visitado="0";
        }
        String select ="SELECT COUNT(*) FROM Puntos;";
        Cursor c=db.rawQuery(select,null);
        c.moveToFirst();
        String id=c.getString(0);
        punto.setId(Integer.parseInt(id));
        c.close();
        String insert= "INSERT INTO Puntos VALUES("+
                id + "," +
                "'" + punto.getNombre() + "'," +
                "'" + punto.getCoorx() + "'," +
                "'" + punto.getCoory() + "'," +
                visitado + ");";
        db.execSQL(insert);

    }
    public void eliminar(Punto punto){
        String delete="DELETE FROM Puntos WHERE id=" + punto.getId() + ";";
        db.execSQL(delete);
    }
    public ArrayList recuperarPuntos(){
        String select="Select * from Puntos;";
        Punto p;
        ArrayList lista=new ArrayList();
        String[] atributos=new String[5];
        Cursor c=db.rawQuery(select,null);
        if (c.moveToFirst()){
            while (c!=null){
                atributos[0]=c.getString(c.getColumnIndex("id"));
                atributos[1]=c.getString(c.getColumnIndex("nombre"));
                atributos[2]=c.getString(c.getColumnIndex("coorX"));
                atributos[3]=c.getString(c.getColumnIndex("coorY"));
                atributos[4]=c.getString(c.getColumnIndex("visitado"));
                if (atributos[4] == "0") {
                    p = new Punto(atributos[1], atributos[2], atributos[3], false);
                }else {
                    p = new Punto(atributos[1], atributos[2], atributos[3], true);
                }
                lista.add(p);
            }
        }
        c.close();
        return lista;
    }
    public void marcarVisto(Punto punto){
        String update="UPDATE Puntos SET visitado=1 where id=" + punto.getId() + ";";
        db.execSQL(update);
    }
}
