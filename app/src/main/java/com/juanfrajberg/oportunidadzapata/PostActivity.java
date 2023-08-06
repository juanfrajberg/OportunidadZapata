package com.juanfrajberg.oportunidadzapata;

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
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class PostActivity extends AppCompatActivity {

    //Flecha para volver atrás
    static ImageView backArrow;

    //Variable para saber si mostrar el Dialog al perderse la conexión
    boolean showWiFiStatus;

    //Variable para saber de qué child (hijo) obtener la información de la DB según el número de publicación
    int postNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Código básico para que se muestre la interfaz
        super.onCreate(savedInstanceState);

        //Switch para saber qué post mostrar
        postNumber = getIntent().getIntExtra("postNumber", 1);
        switch (postNumber) { //No hay un caso "default" porque en la línea de arriba el valor por defecto es 1
            case 1:
                setContentView(R.layout.firstpost_activity);
                break;
            case 2:
                setContentView(R.layout.secondpost_activity);
                break;
            case 3:
                setContentView(R.layout.thirdpost_activity);
                break;
        }

        //Establecer el modo claro como predeterminado incluso con el modo oscuro (ya no es necesario, se configuró el modo oscuro en cada layout)
        //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        //Buscar elementos en layout
        backArrow = findViewById(R.id.post_arrow_imageview);

        //Configurar que se vuelva atrás al hacer clic en la flecha
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Animación del elemento
                YoYo.with(Techniques.Pulse)
                        .duration(450)
                        .repeat(0)
                        .playOn(backArrow);

                finish();
                overridePendingTransition(R.anim.push_up_in, R.anim.push_down_out); //Animación
            }
        });

        //Leer la fecha, título, autores, contenido e imagen de cada publicación del blog desde Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("1JcKn4lV9YC5cF8o_QyekJ7-72u-bRn748CLrLc9jTD0/blog");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            //Este método se llama una vez con el valor inciial y luego cada que vez que la data es actualizada
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Uso un try catch por si hay algún error en la lectura de datos de la DB
                try {
                    //Guardamos los datos en Strings
                    String postTime = dataSnapshot.child(String.valueOf(postNumber)).child("time").getValue(String.class);
                    String postTitle = dataSnapshot.child(String.valueOf(postNumber)).child("title").getValue(String.class);
                    String postWriters = dataSnapshot.child(String.valueOf(postNumber)).child("writers").getValue(String.class);
                    String postContent = dataSnapshot.child(String.valueOf(postNumber)).child("content").getValue(String.class);
                    String postImage = dataSnapshot.child(String.valueOf(postNumber)).child("image").getValue(String.class);

                    //Se le asigna el valor a los elementos del layout
                    TextView postTimeTextView = (TextView) findViewById(R.id.post_time_textview);
                    TextView postTitleTextView = (TextView) findViewById(R.id.post_title_textview);
                    TextView postWritersTextView = (TextView) findViewById(R.id.post_writers_textview);
                    TextView postContentTextView = (TextView) findViewById(R.id.post_content_textview);
                    ImageView postImageImageView = (ImageView) findViewById(R.id.post_image_imageview);

                    postTimeTextView.setText(postTime);
                    postTitleTextView.setText(postTitle);
                    postWritersTextView.setText(postWriters);
                    postContentTextView.setText(Html.fromHtml(postContent));
                    Picasso.get().load(postImage).into(postImageImageView);
                } catch (Exception e) {
                    Log.e("OZ", "" + e);
                }
            }

            @Override
            public void onCancelled(DatabaseError e) {
                //No se pudo leer el valor
            }
        });
    }


    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.push_up_in, R.anim.push_down_out); //Animación
    }

    //Todo este código es para implementar el cartel de la conectividad
    @Override
    protected void onResume() {
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

        super.onResume();
    }

    //Parte del código para detectar si el Wi-Fi se ha conectado o desconectado, acá se cancela el Receiver del Wi-Fi una vez que la actividad pasa al fondo
    @Override
    protected void onStop() {
        try {
            unregisterReceiver(wifiStateReceiver);
        } catch (Exception e) {
        } //Por algún motivo, a veces falla, entonces es mejor usar un try
        super.onStop();
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
                    i = new Intent(PostActivity.this, WiFiActivity.class);
                    i.putExtra("State", "ON");
                    startActivity(i);
                    overridePendingTransition(R.anim.slidezoom_frombottom, R.anim.slidezoom_tobottom); //Animación
                }
            } else {
                //Toast.makeText(getApplicationContext(), "No hay conexión", Toast.LENGTH_SHORT).show();
                if (!WiFiInformation.isActivityVisible() && !WiFiInformation.lastState.equals("OFF") && WiFiInformation.isShowAgain() && showWiFiStatus) {
                    WiFiInformation.lastState = "OFF";
                    i = new Intent(PostActivity.this, WiFiActivity.class);
                    i.putExtra("State", "OFF");
                    startActivity(i);
                    overridePendingTransition(R.anim.slidezoom_frombottom, R.anim.slidezoom_tobottom); //Animación
                }
            }
        }
    };

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

    //Esta función se llama desde WiFiActivity, para que cuando aparezca el Dialog no se pueda hacer clic en los elementos del layout
    public static void setElementsLayoutClickeable(boolean state) {
        backArrow.setClickable(state);
    }
}