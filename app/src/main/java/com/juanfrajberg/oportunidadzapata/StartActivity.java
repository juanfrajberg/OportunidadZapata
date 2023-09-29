package com.juanfrajberg.oportunidadzapata;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;

public class StartActivity extends AppCompatActivity {

    //Botón Iniciar
    static Button iniciarButton;

    //CheckBox Mostrar otra vez
    static CheckBox doNotShowAgainCheckBox;

    //String que se va a usar en SharedPreferences
    String isCheckBoxMarked;

    //Fondo que cubre la actividad
    View coverLayoutView;

    //Variable para saber si mostrar el Dialog al perderse la conexión
    boolean showWiFiStatus;

    //Valores finales de la versión y nombre de la aplicación
    public final String finalAppName = "Oportunidad Zapata";
    public final String finalAppVersion = "1.0.0";

    public static boolean firstRun;

    //Para saber si ya se mostró el Toast que informa sobre el estado de la aplicación
    public boolean alreadyShowedToastVersion = false;

    //Para saber cuál fue el último mensaje sobre la versión mostrado
    private static String messageVersion = "null";
    private static String lastMessageVersion = "nullLast";

    //Acá se detecta si hay que mostrar o no la aplicación
    @Override
    protected void onResume() {
        super.onResume();

        //Prueba para detectar si hay conexión, no si está activo o no el WiFi, como está programado
        //Toast.makeText(getApplicationContext(), "Conectividad: " + isOnline() + ".", Toast.LENGTH_SHORT).show();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        //Parte del código para detectar si el Wi-Fi se ha conectado o desconectado, acá se ejecuta el Receiver cuando el estado del Wi-Fi cambia
        //Este valor se actualiza en WiFiActivity cuando se marca el CheckBox de no "Volver a mostrar"
        showWiFiStatus = preferences.getBoolean("showWiFiStatus", true);

        Handler waitUntilActivityIsLoaded = new Handler();
        waitUntilActivityIsLoaded.postDelayed(new Runnable() {
            public void run() {
                //IntentFilter intentFilter = new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION);
                IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
                registerReceiver(wifiStateReceiver, intentFilter);
            }
        }, 750); //El tiempo que se demora la transición de la actividad

        //Toast.makeText(getApplicationContext(), "Estado final: " + showWiFiStatus + ".", Toast.LENGTH_SHORT).show(); //A modo de prueba

        boolean showActivity = preferences.getBoolean(isCheckBoxMarked, false);
        if (showActivity) {
            coverLayoutView = findViewById(R.id.home_coverscreen_view);
            coverLayoutView.setVisibility(View.VISIBLE);
            startActivity(new Intent(StartActivity.this, HomeActivity.class));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Código básico para que se muestre la interfaz
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_activity);

        Intent intent = getIntent();
        alreadyShowedToastVersion = intent.getBooleanExtra("alreadyShowedToastVersion", false);

        //Establecer el modo claro como predeterminado incluso con el modo oscuro (ya no es necesario, se configuró el modo oscuro en cada layout)
        //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        sendNotification(); //Acá se envía la notificación, antes de detectar si queremos que se muestre o no la actividad

        //Buscar elements en layout
        iniciarButton = findViewById(R.id.start_iniciar_button);
        doNotShowAgainCheckBox = findViewById(R.id.home_donotshowagain_checkbox);

        //Código para detectar si es la primera vez que se ejecuta la aplicación
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        firstRun = preferences.getBoolean("firstRun", true);

        if (firstRun) {
            //Toast de prueba para indicar que es la primera vez corriendo la aplicación
            //Toast.makeText(getApplicationContext(), "Primera vez.", Toast.LENGTH_SHORT).show();

            //Se esconde el checkbox
            doNotShowAgainCheckBox = findViewById(R.id.home_donotshowagain_checkbox);
            doNotShowAgainCheckBox.setVisibility(View.GONE);
        } else {
            //Toast.makeText(getApplicationContext(), "No es la primera vez.", Toast.LENGTH_SHORT).show(); Toast de prueba para indicar que NO es la primera vez corriendo la aplicación

            //Primero escondo el checkbox, y luego lo muestro cuando la transición entre actividades se termina de reproducir
            doNotShowAgainCheckBox.setVisibility(View.GONE);

            //Esperamos a que cargue la actividad para mostrar su animación
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    //Animación del checkbox
                    YoYo.with(Techniques.FadeInLeft)
                            .duration(550)
                            .repeat(0)
                            .playOn(doNotShowAgainCheckBox);
                    doNotShowAgainCheckBox.setVisibility(View.VISIBLE);
                }
            }, 750); //Esto es lo que demora la animación del cambio de actividad (fade_in)
        }

        //Guardamos su valor como falso, si esto ya se ejecutó entonces la próxima no será la primera vez en esta pantalla
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("firstRun", Boolean.FALSE);
        editor.commit();

        //Abrir la pantalla de casa
        iniciarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!WiFiInformation.isActivityVisible()) { //Posible solución
                    //Animación del botón
                    YoYo.with(Techniques.Pulse)
                            .duration(450)
                            .repeat(0)
                            .playOn(iniciarButton);

                    //Se abre la actividad de Home (casa)
                    startActivity(new Intent(StartActivity.this, HomeActivity.class));
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out); //Animación
                }
            }
        });

        //Con esta función, cada vez que se marque o no el checkbox se actualiza el valor en SharedPreferences
        //Antes lo hacía al hacer clic en el botón de inicio, pero así es mucho más eficaz
        doNotShowAgainCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton checkBox, boolean b) {
                //Código para guardar el valor del CheckBox
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                SharedPreferences.Editor editor = preferences.edit();
                if (checkBox.isChecked()) {
                    editor.putBoolean(isCheckBoxMarked, Boolean.TRUE);
                } else {
                    editor.putBoolean(isCheckBoxMarked, Boolean.FALSE);
                }
                editor.apply(); //Al final guardamos los cambios
            }
        });

        //Detectar si es la última versión de la aplicación
        DatabaseReference databaseReference;
        databaseReference = FirebaseDatabase.getInstance().getReference("1JcKn4lV9YC5cF8o_QyekJ7-72u-bRn748CLrLc9jTD0/application/1");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    String appNameFirebase = snapshot.child("appName").getValue(String.class);
                    String lastVersionFirebase = snapshot.child("lastVersion").getValue(String.class);

                    //Verificar si se está usando la última versión de la aplicación y coincide el nombre
                    if (appNameFirebase.equals(finalAppName) && lastVersionFirebase.equals(finalAppVersion)) {
                        messageVersion = "Updated";
                        if (!lastMessageVersion.equals(messageVersion)) {
                            Toast.makeText(getApplicationContext(), "¡Estás usando la última versión de la aplicación!", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        messageVersion = "Not updated";
                        if (!lastMessageVersion.equals(messageVersion)) {
                            Toast.makeText(getApplicationContext(), "¡No te olvides de actualizar la aplicación, esta no es la última versión!", Toast.LENGTH_LONG).show();
                        }
                    }
                    lastMessageVersion = messageVersion;

                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "No se pudo verificar si estás usando la última versión de la aplicación.", Toast.LENGTH_LONG).show();

                    messageVersion = "Error";
                    lastMessageVersion = messageVersion;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "No se pudo verificar si estás usando la última versión de la aplicación.", Toast.LENGTH_LONG).show();

                messageVersion = "Error";
                lastMessageVersion = messageVersion;
            }
        });
    }

    //Parte del código para detectar si el Wi-Fi se ha conectado o desconectado, este es el Receiver que detecta si el Wifi se conectó o desconectó
    public BroadcastReceiver wifiStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Este método verifica si hay conexión a Internet, no se si activó o desactivó el Wi-Fi
            Intent i; //Se usa en ambos casos, por lo que lo defino acá
            //Lo mismo ocurre con estas dos líneas
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();

            //Si hay conectividad se abre la ventana de que tenemos Internet, y viceversa
            if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                //Toast.makeText(getApplicationContext(), "Hay conexión", Toast.LENGTH_SHORT).show();
                if (!WiFiInformation.isActivityVisible() && !WiFiInformation.lastState.equals("ON")) { //&& WiFiInformation.isShowAgain()
                    WiFiInformation.lastState = "ON";
                    i = new Intent(StartActivity.this, WiFiActivity.class);
                    i.putExtra("State", "ON");
                    startActivity(i);
                    overridePendingTransition(R.anim.slidezoom_frombottom, R.anim.slidezoom_tobottom); //Animación
                }
            } else {
                //Toast.makeText(getApplicationContext(), "No hay conexión", Toast.LENGTH_SHORT).show();
                if (!WiFiInformation.isActivityVisible() && !WiFiInformation.lastState.equals("OFF") && WiFiInformation.isShowAgain() && showWiFiStatus) {
                    WiFiInformation.lastState = "OFF";
                    i = new Intent(StartActivity.this, WiFiActivity.class);
                    i.putExtra("State", "OFF");
                    startActivity(i);
                    overridePendingTransition(R.anim.slidezoom_frombottom, R.anim.slidezoom_tobottom); //Animación
                }
            }
        }
    };

    //Parte del código para detectar si el Wi-Fi se ha conectado o desconectado, acá se cancela el Receiver del Wi-Fi una vez que la actividad pasa al fondo
    @Override
    protected void onStop() {
        try {
            unregisterReceiver(wifiStateReceiver);
        } catch (Exception e) {
        } //Por algún motivo, a veces falla, entonces es mejor usar un try
        super.onStop();
    }

    //Para que cuando se presione el botón de atrás, se cierre todo
    @Override
    public void onBackPressed() {
        finishAffinity(); //Cierra la aplicación pero se mantiene en segundo plano
        //finish(); no funcionaba correctamente
    }

    //Función de prueba para detectar si hay conexión, no si está activo o no el WiFi, como está programado
    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }

    public void sendNotification() {
        //Enviar una notificación diaria
        try {
            Intent intent = new Intent(StartActivity.this, NotificationBroadcast.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(StartActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

            //Hora a la cual queremos que se envíe la notificación
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, getApplicationContext().getResources().getInteger(R.integer.notification_hour)); //Formato 24 horas
            calendar.set(Calendar.MINUTE, getApplicationContext().getResources().getInteger(R.integer.notification_minute));
            calendar.set(Calendar.SECOND, getApplicationContext().getResources().getInteger(R.integer.notification_second)); //Este valor debe ser siempre 0, porque sino por algún motivo la notificación aparece en un segundo aleatorio entre 0 y 59

            //Para comparar el tiempo en el que sefinió la alarma y la hora actual, por lo que si se ya se pasó la hora indicada, se ejecutará al día siguiente
            if (calendar.getTime().compareTo(new Date()) < 0)
                calendar.add(Calendar.DAY_OF_MONTH, 1);

            //Se ejecuta la alarma
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent); //Consume más batería que setInexactRepeating() pero es mucho más preciso
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "No se pudo enviar la notificación.", Toast.LENGTH_SHORT).show();
            //Log.e("error tag", Log.getStackTraceString(e));
        }
    }

    //Esta función se llama desde WiFiActivity, para que cuando aparezca el Dialog no se pueda hacer clic en los elementos del layout
    public static void setElementsLayoutClickeable(boolean state) {
        iniciarButton.setClickable(state);
        doNotShowAgainCheckBox.setClickable(state);
    }
}