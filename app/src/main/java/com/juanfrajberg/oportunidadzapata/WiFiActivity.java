package com.juanfrajberg.oportunidadzapata;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class WiFiActivity extends AppCompatActivity {

    //Botón para cerrar la actividad (Dialog)
    static ImageView closeButton;
    static CheckBox showAgainCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Código básico para que se muestre la interfaz
        super.onCreate(savedInstanceState);

        //Nota: en el Manifest no se especifica la screenOrientation
        //Esto es porque en Android Oreo, como en el caso del J5 Prime, al hacerlo la aplicación se traba

        Bundle infoReceived = getIntent().getExtras();
        if (infoReceived != null) {
            if (infoReceived.getString("State").equals("ON"))
                setContentView(R.layout.wifion_dialog);
            if (infoReceived.getString("State").equals("OFF"))
                setContentView(R.layout.wifioff_dialog);
        }

        //Se ajusta el tamaño de la actividad (Dialog)
        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.95);
        getWindow().setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT);
        //Se hace no modal, para que podamos recibir eventos cuando se hace clic dentro de la actividad (Dialog)
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        //Pero nos notifica cuando se hace clic afuera
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);

        //Establecer el modo claro como predeterminado incluso con el modo oscuro (ya no es necesario, se configuró el modo oscuro en cada layout)
        //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        //Buscar elementos en layout
        //Acá se detecta que layout se está mostrando, para saber qué cruz (y CheckBox) se debe usar para cerrar la actividad (Dialog)
        if (infoReceived != null) {
            if (infoReceived.getString("State").equals("ON"))
                closeButton = findViewById(R.id.wifion_crossclose_imageview);
            if (infoReceived.getString("State").equals("OFF")) {
                closeButton = findViewById(R.id.wifioff_crossclose_imageview);
                showAgainCheckBox = findViewById(R.id.wifioff_donotshowagain_checkbox);
            }
        }

        //Esta línea de código es para que al hacer clic fuera de esta actividad, que se usa como Dialog, se cierre
        this.setFinishOnTouchOutside(false);

        //Cerrar la actividad (Dialog) al hacer clic en la cruz
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.slidezoom_frombottom, R.anim.slidezoom_tobottom); //Animación
            }
        });

        if (infoReceived != null) {
            if (infoReceived.getString("State").equals("OFF")) {
                showAgainCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (!isChecked) WiFiInformation.showAgain = true;
                        if (isChecked) {
                            WiFiInformation.showAgain = false;

                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putBoolean("showWiFiStatus", Boolean.FALSE);
                            editor.apply();
                        }
                        //Toast.makeText(getApplicationContext(), "Estado desde Wi-Fi: " + WiFiInformation.showAgain + ".",
                        //        Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    //Esta función es por si el usuario hace clic afuera, usando this.setFinishOnTouchOutside(false) la animación no se reproduce correctamente
    //Se ejecuta correctamente, pero el usuario puede hacer clic afuera inintencionalmente y se cerrará
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //Cuando se recibe una notificación de que el usuario ha hecho clic afuera de la app, se finaliza
        if (MotionEvent.ACTION_OUTSIDE == event.getAction()) {
            if (WiFiInformation.isActivityVisible()) {
                finish();
                overridePendingTransition(R.anim.slidezoom_frombottom, R.anim.slidezoom_tobottom); //Animación
                return true;
            }
        }
        //Delega todo lo demás a la Actividad
        return super.onTouchEvent(event);
    }

    //Estas líneas mandan la información a otra clase, para detectar en StartActivity si se está mostrando o no la actividad (Dialog)
    @Override
    protected void onResume() {
        super.onResume();
        WiFiInformation.activityResumed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        WiFiInformation.activityPaused();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slidezoom_frombottom, R.anim.slidezoom_tobottom); //Animación
    }
}