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
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class BlogActivity extends AppCompatActivity {

    //Flecha para volver atrás
    static ImageView backArrow;

    //Layouts (cajas) de los posts del Blog
    static RelativeLayout firstExampleView;
    static RelativeLayout secondExampleView;
    static RelativeLayout thirdExampleView;

    //Variable para saber si mostrar el Dialog al perderse la conexión
    boolean showWiFiStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Código básico para que se muestre la interfaz
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blog_activity);

        //Establecer el modo claro como predeterminado incluso con el modo oscuro (ya no es necesario, se configuró el modo oscuro en cada layout)
        //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        //Buscar elementos en layout
        backArrow = findViewById(R.id.blog_arrow_imageview);

        firstExampleView = findViewById(R.id.blog_firstexamplepostview_relativelayout);
        secondExampleView = findViewById(R.id.blog_secondexamplepostview_relativelayout);
        thirdExampleView = findViewById(R.id.blog_thirdexamplepostview_relativelayout);

        //Animar las tres publicaciones al aparecer (esta función también se ejecuta al presionar el botón atrás en PostActivity)
        //animateViews(); Esta línea no hace falta porque ya se ejecuta en onResume()

        //Configurar que se vuelva atrás al hacer clic en la flecha
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.push_up_in, R.anim.push_down_out); //Animación

                //Animación del elemento
                YoYo.with(Techniques.Pulse)
                        .duration(450)
                        .repeat(0)
                        .playOn(backArrow);
            }
        });

        //Configurar que se abra el post de ejemplo al hacer clic en las tres layouts
        firstExampleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BlogActivity.this, PostActivity.class);
                intent.putExtra("postNumber", 1);
                startActivity(intent);
                overridePendingTransition(R.anim.push_up_in, R.anim.push_down_out);

                YoYo.with(Techniques.RollOut)
                        .duration(450)
                        .repeat(0)
                        .playOn(firstExampleView);
            }
        });

        secondExampleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BlogActivity.this, PostActivity.class);
                intent.putExtra("postNumber", 2);
                startActivity(intent);
                overridePendingTransition(R.anim.push_up_in, R.anim.push_down_out);

                YoYo.with(Techniques.RollOut)
                        .duration(450)
                        .repeat(0)
                        .playOn(secondExampleView);
            }
        });

        thirdExampleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BlogActivity.this, PostActivity.class);
                intent.putExtra("postNumber", 3);
                startActivity(intent);
                overridePendingTransition(R.anim.push_up_in, R.anim.push_down_out);

                YoYo.with(Techniques.RollOut)
                        .duration(450)
                        .repeat(0)
                        .playOn(thirdExampleView);
            }
        });

        //Leer el título, escritor y descripción de cada publicación del blog desde Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("1JcKn4lV9YC5cF8o_QyekJ7-72u-bRn748CLrLc9jTD0/blog");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            //Este método se llama una vez con el valor inciial y luego cada que vez que la data es actualizada
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Uso un try catch por si hay algún error en la lectura de datos de la DB
                try {
                    //Guardamos los datos en Strings
                    String firstPostTitle = dataSnapshot.child("1").child("title").getValue(String.class);
                    String secondPostTitle = dataSnapshot.child("2").child("title").getValue(String.class);
                    String thirdPostTitle = dataSnapshot.child("3").child("title").getValue(String.class);

                    String firstPostWriters = dataSnapshot.child("1").child("writers").getValue(String.class);
                    String secondPostWriters = dataSnapshot.child("2").child("writers").getValue(String.class);
                    String thirdPostWriters = dataSnapshot.child("3").child("writers").getValue(String.class);

                    String firstPostDescription = dataSnapshot.child("1").child("description").getValue(String.class);
                    String secondPostDescription = dataSnapshot.child("2").child("description").getValue(String.class);
                    String thirdPostDescription = dataSnapshot.child("3").child("description").getValue(String.class);

                    //Se le asigna el valor a los elementos del layout
                    TextView firstPostTitleTextView = (TextView) findViewById(R.id.blog_firstexampleposttitlerl_textview);
                    TextView secondPostTitleTextView = (TextView) findViewById(R.id.blog_secondexampleposttitlerl_textview);
                    TextView thirdPostTitleTextView = (TextView) findViewById(R.id.blog_thirdexampleposttitlerl_textview);
                    firstPostTitleTextView.setText(firstPostTitle);
                    secondPostTitleTextView.setText(secondPostTitle);
                    thirdPostTitleTextView.setText(thirdPostTitle);

                    TextView firstPostWritersTextView = (TextView) findViewById(R.id.blog_firstexamplepostwritersrl_textview);
                    TextView secondPostWritersTextView = (TextView) findViewById(R.id.blog_secondexamplepostwritersrl_textview);
                    TextView thirdPostWritersTextView = (TextView) findViewById(R.id.blog_thirdexamplepostwritersrl_textview);
                    firstPostWritersTextView.setText(firstPostWriters);
                    secondPostWritersTextView.setText(secondPostWriters);
                    thirdPostWritersTextView.setText(thirdPostWriters);

                    TextView firstPostDescriptionTextView = (TextView) findViewById(R.id.blog_firstexamplepostdescriptionrl_textview);
                    TextView secondPostDescriptionTextView = (TextView) findViewById(R.id.blog_secondexamplepostdescriptionrl_textview);
                    TextView thirdPostDescriptionTextView = (TextView) findViewById(R.id.blog_thirdexamplepostdescriptionrl_textview);
                    firstPostDescriptionTextView.setText(Html.fromHtml(firstPostDescription));
                    secondPostDescriptionTextView.setText(Html.fromHtml(secondPostDescription));
                    thirdPostDescriptionTextView.setText(Html.fromHtml(thirdPostDescription));
                } catch (Exception e) {}
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

    //Función para animar las vistas y que esté más ordenado
    public void animateViews() {
        YoYo.with(Techniques.RollIn)
                .duration(450)
                .repeat(0)
                .playOn(firstExampleView);

        YoYo.with(Techniques.RollIn)
                .duration(450)
                .repeat(0)
                .playOn(secondExampleView);

        YoYo.with(Techniques.RollIn)
                .duration(450)
                .repeat(0)
                .playOn(thirdExampleView);
    }

    //Todo este código es para implementar el cartel de la conectividad
    @Override
    protected void onResume() {
        animateViews(); //Esto es debido a que cuando vuelve de la publicación, no se ve el post porque se reprodujo la animación de salida

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
                    i = new Intent(BlogActivity.this, WiFiActivity.class);
                    i.putExtra("State", "ON");
                    startActivity(i);
                    overridePendingTransition(R.anim.slidezoom_frombottom, R.anim.slidezoom_tobottom); //Animación
                }
            } else {
                //Toast.makeText(getApplicationContext(), "No hay conexión", Toast.LENGTH_SHORT).show();
                if (!WiFiInformation.isActivityVisible() && !WiFiInformation.lastState.equals("OFF") && WiFiInformation.isShowAgain() && showWiFiStatus) {
                    WiFiInformation.lastState = "OFF";
                    i = new Intent(BlogActivity.this, WiFiActivity.class);
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
        firstExampleView.setClickable(state);
        secondExampleView.setClickable(state);
        thirdExampleView.setClickable(state);
    }
}