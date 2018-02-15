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

    /**
     * Constructor en el cual se abre la base de datos privada PuntosDB o la genera si no existe
     * Ademas de crear la tabla Puntos en caso de que no exista
     * @param act
     */
    public GestionDB(ActivityMain act) {
        this.db=act.openOrCreateDatabase("PuntosDB",act.MODE_PRIVATE,null);
        crearTabla();//metodo encargado de crear la tabla si no existe
    }
    public void crearTabla(){

        //db.execSQL("delete from puntos");
        String crear="CREATE TABLE IF NOT EXISTS Puntos(id INTEGER PRIMARY KEY, nombre VARCHAR(100),coorX VARCHAR(250), coorY VARCHAR(250), visitado INTEGER );";//sql de creacion de tabla
        db.execSQL(crear);//ejecucion del sql

    }

    /**
     * Metodo encargado de insertar un Punto pasado por parametro a la tabla Puntos de la base de datos
     * @param punto
     */
    public void insertar(Punto punto){
        String visitado;//string el cual se insertara en la columna visitado
        if (punto.isVisitado()){//si ha sido visitado
            visitado="1";
        }else {//sino
            visitado="0";
        }
        int id;
        Cursor c=null;
        //select del contador de puntos existentes para generar siguiente id(autoincremental)
        String select ="SELECT max(id) FROM Puntos;";

        try{
            //como el cursor solo tiene una fila no hace falta hacer un bucle
           c = db.rawQuery(select,null);
            c.moveToFirst();
            //cogemos el primer y unico resultado de la fila que sera el id del punto a insertar
            id=c.getInt(0)+1;
            //le damos el id al objeto punto
        }catch(Exception ex){
            id = 1;
        }

        if (c!= null)c.close();

        String insert= "INSERT INTO Puntos VALUES("+
                id + "," +
                "'" + punto.getNombre() + "'," +
                "'" + punto.getCoorx() + "'," +
                "'" + punto.getCoory() + "'," +
                visitado + ");";//Sentencia Insert del punto dado
        db.execSQL(insert);//ejecutar insert

    }

    /**
     * metodo encargado de borrar de la BD el punto dado por parametro
     * @param punto
     */
    public void eliminar(Punto punto){
        String delete="DELETE FROM Puntos WHERE coorX='" + punto.getCoorx() + "' AND coorY='"+ punto.getCoory() + "';";//Sentencia de delete del punto dado
        db.execSQL(delete);//ejecutar delete
    }

    /**
     * metodo encargado de recorrer las filas de la tabla Puntos
     * @return ArrayList lista el cual contiene Objetos Punto
     */
    public ArrayList recuperarPuntos(){
        String select="Select * from Puntos;";//Consulta select
        Punto p;//objeto contenedor de puntos a agregar a la lista
        ArrayList lista=new ArrayList();//lista que devolveremos
        String[] atributos=new String[5];//contenedor de atributos del objeto Punto
        Cursor c=db.rawQuery(select,null);
        if (c.moveToFirst()){//si el cursor contiene datos
            int i= 0;
            while (i< c.getCount()){
                atributos[0]=c.getString(c.getColumnIndex("id"));
                atributos[1]=c.getString(c.getColumnIndex("nombre"));
                atributos[2]=c.getString(c.getColumnIndex("coorX"));
                atributos[3]=c.getString(c.getColumnIndex("coorY"));
                atributos[4]=c.getString(c.getColumnIndex("visitado"));
                //si es cero el atributo visitado sera false
                if (atributos[4].equalsIgnoreCase("0")) {
                    p = new Punto(atributos[1], atributos[2], atributos[3], false);
                }else {//si no sera true
                    p = new Punto(atributos[1], atributos[2], atributos[3], true);
                }
                lista.add(p);//Se agrega el punto generado a la lista
                c.moveToNext();//Se mueve al siguiente registro del cursor
                i++;//se incremente el iterador del bucle
            }
        }
        c.close();//se cierra el cursor
        return lista;//devuelve la lista
    }

    /**
     * Actualiza el punto dado y lo pone como visitado
     * @param punto
     */
    public void marcarVisto(Punto punto){
        //sentencia update del punto dado
        String update="UPDATE Puntos SET visitado=1 where coorX='" + punto.getCoorx() + "' AND coorY='"+ punto.getCoory() + "';";
        db.execSQL(update);
    }
}
