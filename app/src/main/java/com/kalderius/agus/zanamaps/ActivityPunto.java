package com.kalderius.agus.zanamaps;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class ActivityPunto extends AppCompatActivity {
    //Declaramos los elementos de la vista
    EditText nomb;
    CheckBox check;
    Button guarda;
    //Creamos el Bundle y las variables para crear el punto
    Bundle b;
    String coorx,coory;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_punto);
        //Asignamos los elementos de la vista
        nomb=findViewById(R.id.nom);
        check=findViewById(R.id.visi);
        guarda=findViewById(R.id.guarda);

        //Recogemos los datos del intent que nos invoca
        b = this.getIntent().getExtras();
        coorx=b.getString("coorx");
        coory=b.getString("coory");
    }

    private void guarda(){
        String nombre=nomb.getText().toString();
        boolean visitado=check.isChecked();

        Punto punto=new Punto(nombre,coorx,coory,visitado);

        Intent intent= getIntent();

        intent.putExtra("punto",punto);

        setResult(RESULT_OK,intent);

        finish();
    }

}
