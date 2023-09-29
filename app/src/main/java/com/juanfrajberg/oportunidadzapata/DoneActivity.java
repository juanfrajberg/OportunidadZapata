package com.juanfrajberg.oportunidadzapata;

import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class DoneActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Código básico para que se muestre la interfaz
        super.onCreate(savedInstanceState);
        setContentView(R.layout.done_activity);

        //Establecer el modo claro como predeterminado incluso con el modo oscuro
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        //Método para cerrar la pantalla luego de 3,5 segundos que se abrió
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                finish();
                //La línea de código de abajo era para mostrar que funcionaba el traspaso de datos
                //startActivity(new Intent(DoneActivity.this, DataActivity.class));
            }
        }, 7850);
    }

    //Función para que no se pueda cerrar la pantalla con el botón atrás
    @Override
    public void onBackPressed() {
        return;
    }
}