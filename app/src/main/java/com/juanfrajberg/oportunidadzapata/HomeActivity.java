package com.juanfrajberg.oportunidadzapata;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import pl.droidsonroids.gif.GifImageView;

public class HomeActivity extends AppCompatActivity {

    //Botones de las ventanas
    static Button requisitosButton; //Botón de requisitos
    static Button pasosaseguirButton; //Botón de pasos a seguir
    static Button nosotrosButton; //Botón de nosotros
    static Button apoyanosButton; //Botón de nosotros

    //Botones de las distintas pestañas
    static ImageView contactButton; //Botón de contacto
    static ImageView proposalButton; //Botón de propuestas

    //Pestaña de búsqueda
    static EditText searchTabEditText;

    //Imagen y texto para hacer clic e ir al Blog
    static ImageView schoolImageView;
    static TextView schoolTextView;

    //Imagen que simula ser el nagevador desde el que se ejecuta Poe
    static ImageView poeAI;

    //Elementos para dirigirse a la actividad de hablar con la AI
    static RelativeLayout AIrelativeLayout;
    static View AIview;
    static GifImageView AIGIF;

    //Botones extra de la actividad
    static LinearLayout shareAppButton; //Botón para compartir la app
    static LinearLayout contactUsButton; //Botón para contactarse con los desarrolladores

    //Variable para saber si mostrar el Dialog al perderse la conexión
    boolean showWiFiStatus;

    //BottomSheetDialog de Requisitos
    BottomSheetDialog requisitosDialog;
    BottomSheetDialog pasosaseguirDialog;
    BottomSheetDialog nosotrosDialog;
    BottomSheetDialog apoyanosDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Código básico para que se muestre la interfaz
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

        //Establecer el modo claro como predeterminado incluso con el modo oscuro (ya no es necesario, se configuró el modo oscuro en cada layout)
        //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        //Buscar elementos en layout
        requisitosButton = findViewById(R.id.home_requisitos_button);
        pasosaseguirButton = findViewById(R.id.home_pasosaseguir_button);
        nosotrosButton = findViewById(R.id.home_nosotros_button);
        apoyanosButton = findViewById(R.id.home_apoyanos_button);

        contactButton = findViewById(R.id.home_contact_imageview);
        proposalButton = findViewById(R.id.home_proposal_imageview);

        searchTabEditText = findViewById(R.id.home_searchtab_edittext);

        schoolImageView = findViewById(R.id.home_school_imageview);
        schoolTextView = findViewById(R.id.home_school_textview);

        poeAI = findViewById(R.id.home_browserAI_imageview);

        AIrelativeLayout = findViewById(R.id.home_aimessage_relativelayout);
        AIview = findViewById(R.id.home_aiiconbackground_view);
        AIGIF = findViewById(R.id.home_aiicongif_gif);

        shareAppButton = findViewById(R.id.home_shareapp_linearlayout);
        contactUsButton = findViewById(R.id.home_contactus_linearlayout);

        //Funciones al hacer clic en los botones

        //Botones de las ventanas
        //Requisitos
        requisitosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Mostrar su respectivo Bottom Sheet Dialog
                View v = getLayoutInflater().inflate(R.layout.home_bottomsheet_requisitos, null);
                requisitosDialog = new BottomSheetDialog(HomeActivity.this, R.style.BottomSheetDialog); // Style here
                requisitosDialog.setContentView(v);
                requisitosDialog.show();

                //Animación del elemento
                YoYo.with(Techniques.Pulse)
                        .duration(450)
                        .repeat(0)
                        .playOn(requisitosButton);

                //Se centra el ScrollView en ese elemento
                HorizontalScrollView buttonsScrollView = (HorizontalScrollView) findViewById(R.id.home_buttonsscroll_horizontalscrollview);
                buttonsScrollView.smoothScrollTo(requisitosButton.getLeft(), 0);
            }
        });

        //Pasos a seguir
        pasosaseguirButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Mostrar su respectivo Bottom Sheet Dialog
                View v = getLayoutInflater().inflate(R.layout.home_bottomsheet_pasosaseguir, null);
                pasosaseguirDialog = new BottomSheetDialog(HomeActivity.this, R.style.BottomSheetDialog); // Style here
                pasosaseguirDialog.setContentView(v);
                pasosaseguirDialog.show();

                //Animación del elemento
                YoYo.with(Techniques.Pulse)
                        .duration(450)
                        .repeat(0)
                        .playOn(pasosaseguirButton);

                //Se centra el ScrollView en ese elemento
                HorizontalScrollView buttonsScrollView = (HorizontalScrollView) findViewById(R.id.home_buttonsscroll_horizontalscrollview);
                buttonsScrollView.smoothScrollTo(pasosaseguirButton.getLeft(), 0);
            }
        });

        //Nosotros
        nosotrosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Mostrar su respectivo Bottom Sheet Dialog
                View v = getLayoutInflater().inflate(R.layout.home_bottomsheet_nosotros, null);
                nosotrosDialog = new BottomSheetDialog(HomeActivity.this, R.style.BottomSheetDialog); // Style here
                nosotrosDialog.setContentView(v);
                nosotrosDialog.show();

                //Animación del elemento
                YoYo.with(Techniques.Pulse)
                        .duration(450)
                        .repeat(0)
                        .playOn(nosotrosButton);

                //Se centra el ScrollView en ese elemento
                HorizontalScrollView buttonsScrollView = (HorizontalScrollView) findViewById(R.id.home_buttonsscroll_horizontalscrollview);
                buttonsScrollView.smoothScrollTo(nosotrosButton.getLeft(), 0);
            }
        });

        apoyanosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Mostrar su respectivo Bottom Sheet Dialog
                View v = getLayoutInflater().inflate(R.layout.home_bottomsheet_apoyanos, null);
                apoyanosDialog = new BottomSheetDialog(HomeActivity.this, R.style.BottomSheetDialog); // Style here
                apoyanosDialog.setContentView(v);
                apoyanosDialog.show();

                //Animación del elemento
                YoYo.with(Techniques.Pulse)
                        .duration(450)
                        .repeat(0)
                        .playOn(apoyanosButton);

                //Se centra el ScrollView en ese elemento
                HorizontalScrollView buttonsScrollView = (HorizontalScrollView) findViewById(R.id.home_buttonsscroll_horizontalscrollview);
                buttonsScrollView.smoothScrollTo(apoyanosButton.getLeft(), 0);
            }
        });

        //Botones de las distintas pestañas
        contactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Iniciar la pestaña de contacto
                startActivity(new Intent(HomeActivity.this, ContactActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left); //Animación

                //Animación del botón
                YoYo.with(Techniques.Pulse)
                        .duration(450)
                        .repeat(0)
                        .playOn(contactButton);
            }
        });

        proposalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Iniciar la pestaña de propuestas
                startActivity(new Intent(HomeActivity.this, ProposalActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left); //Animación

                //Animación del botón
                YoYo.with(Techniques.Pulse)
                        .duration(450)
                        .repeat(0)
                        .playOn(proposalButton);
            }
        });

        //Pestaña de búsqueda
        searchTabEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    //Limpiar el focus de este elemento al hacer clic en el botón de OK o terminado
                    searchTabEditText.clearFocus();
                }
                return false;
            }
        });

        //Abrir blog
        schoolImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, BlogActivity.class));
                overridePendingTransition(R.anim.push_up_in, R.anim.push_down_out);

                //Animación del elemento
                YoYo.with(Techniques.Pulse)
                        .duration(450)
                        .repeat(0)
                        .playOn(schoolImageView);
            }
        });

        schoolTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, BlogActivity.class));
                overridePendingTransition(R.anim.push_up_in, R.anim.push_down_out);

                //Animación del botón
                YoYo.with(Techniques.Pulse)
                        .duration(450)
                        .repeat(0)
                        .playOn(schoolImageView);
            }
        });

        //Configurar la imagen para poder usar la AI de Oportunidad Zapata desde Poe (https://poe.com/OZapata)

        poeAI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://poe.com/OZapata";
                Intent openBrowser = new Intent(Intent.ACTION_VIEW);
                openBrowser.setData(Uri.parse(url));
                startActivity(openBrowser);

                //Animación del elemento
                YoYo.with(Techniques.Pulse)
                        .duration(450)
                        .repeat(0)
                        .playOn(poeAI);
            }
        });

        //Configurar los clics en los elementos para ir a hablar con la AI
        AIrelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openConsultasActivity = new Intent(HomeActivity.this, ConsultasActivity.class);
                openConsultasActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(openConsultasActivity);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out); //Animación
                //startActivity(new Intent(HomeActivity.this, ConsultasActivity.class));

                //Animación del elemento
                YoYo.with(Techniques.Pulse)
                        .duration(450)
                        .repeat(0)
                        .playOn(AIrelativeLayout);
            }
        });

        AIview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openConsultasActivity = new Intent(HomeActivity.this, ConsultasActivity.class);
                openConsultasActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(openConsultasActivity);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out); //Animación
                //startActivity(new Intent(HomeActivity.this, ConsultasActivity.class));

                //Animación del elemento (acá animo el layout, es decir el mensaje, porque queda mejor)
                YoYo.with(Techniques.Pulse)
                        .duration(450)
                        .repeat(0)
                        .playOn(AIrelativeLayout);
            }
        });

        AIGIF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openConsultasActivity = new Intent(HomeActivity.this, ConsultasActivity.class);
                openConsultasActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(openConsultasActivity);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out); //Animación
                //startActivity(new Intent(HomeActivity.this, ConsultasActivity.class));

                //Animación del elemento (acá animo el layout, es decir el mensaje, porque queda mejor)
                YoYo.with(Techniques.Pulse)
                        .duration(450)
                        .repeat(0)
                        .playOn(AIrelativeLayout);
            }
        });

        shareAppButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Oportunidad Zapata");
                    String shareMessage = getApplicationContext().getResources().getString(R.string.home_shareappmessage_text);
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                    startActivity(Intent.createChooser(shareIntent, "Por favor, elige una aplicación para compartir Oportunidad Zapata."));
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "No se pudo compartir la aplicación.", Toast.LENGTH_SHORT).show();
                }

                //Animación del elemento
                YoYo.with(Techniques.Pulse)
                        .duration(450)
                        .repeat(0)
                        .playOn(shareAppButton);
            }
        });

        contactUsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Animación del botón
                YoYo.with(Techniques.Pulse)
                        .duration(450)
                        .repeat(0)
                        .playOn(contactUsButton);

                //Creación del builder para destinatario
                Uri.Builder builder1 = new Uri.Builder();
                builder1.scheme("mailto");
                builder1.opaquePart("oportunidadzapata@gmail.com");

                //Creación del builder para destinatario
                Uri.Builder builder2 = new Uri.Builder();
                builder2.appendQueryParameter("subject", getApplicationContext().getResources().getString(R.string.home_contactus_subjectmail));
                builder2.appendQueryParameter("body", getApplicationContext().getResources().getString(R.string.home_contactusbodymail));

                //Conversión de ambos builders a uno solo
                Uri uri = Uri.parse(builder1.toString() + builder2.toString());

                //Envío del mail y selección de aplicación para hacerlo
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, uri);
                startActivity(Intent.createChooser(emailIntent, "Por favor, elige una aplicación para enviar un correo a Oportunidad Zapata."));
            }
        });

        //Esconder el teclado cuando cambia el focus
        //Hacerlo de esta forma es lo más óptimo porque permite que me desplace por el ScrollView
        searchTabEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        });
    }

    //Función que se ejecuta al hacer clic en el texto "oportunidadzapata@gmail.com"
    public void onClickTextMail(View v) {
        if (!WiFiInformation.isActivityVisible()) { //Si la actividad (Dialog) que informa sobre la conexión no está abierto
            //Creación del builder para destinatario
            Uri.Builder builder1 = new Uri.Builder();
            builder1.scheme("mailto");
            builder1.opaquePart("oportunidadzapata@gmail.com");

            //Creación del builder para destinatario
            Uri.Builder builder2 = new Uri.Builder();
            builder2.appendQueryParameter("subject", "Duda sobre Oportunidad Zapata");
            builder2.appendQueryParameter("body", "Hola, me contacto con ustedes para preguntarles sobre ...\n¡Muchas gracias!");

            //Conversión de ambos builders a uno solo
            Uri uri = Uri.parse(builder1.toString() + builder2.toString());

            //Envío del mail y selección de aplicación para hacerlo
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, uri);
            startActivity(Intent.createChooser(emailIntent, "Por favor, elige una aplicación para enviar un correo."));
        }
    }

    //Función para que cuando el usuario vuelva atrás, vuelva a la pantalla de Start y no a alguna
    //otra actividad. También para que haya una animación
    @Override
    public void onBackPressed() {
        startActivity(new Intent(HomeActivity.this, StartActivity.class));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
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

                    if (requisitosDialog != null) requisitosDialog.dismiss();
                    if (pasosaseguirDialog != null) pasosaseguirDialog.dismiss();
                    if (nosotrosDialog != null) nosotrosDialog.dismiss();
                    if (apoyanosDialog != null) apoyanosDialog.dismiss();

                    i = new Intent(HomeActivity.this, WiFiActivity.class);
                    i.putExtra("State", "ON");
                    startActivity(i);
                    overridePendingTransition(R.anim.slidezoom_frombottom, R.anim.slidezoom_tobottom); //Animación
                }
            } else {
                //Toast.makeText(getApplicationContext(), "No hay conexión", Toast.LENGTH_SHORT).show();
                if (!WiFiInformation.isActivityVisible() && !WiFiInformation.lastState.equals("OFF") && WiFiInformation.isShowAgain() && showWiFiStatus) {
                    WiFiInformation.lastState = "OFF";

                    if (requisitosDialog != null) requisitosDialog.dismiss();
                    if (pasosaseguirDialog != null) pasosaseguirDialog.dismiss();
                    if (nosotrosDialog != null) nosotrosDialog.dismiss();

                    i = new Intent(HomeActivity.this, WiFiActivity.class);
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
        requisitosButton.setClickable(state);
        pasosaseguirButton.setClickable(state);
        nosotrosButton.setClickable(state);
        apoyanosButton.setClickable(state);
        contactButton.setClickable(state);
        proposalButton.setClickable(state);
        searchTabEditText.setClickable(state);
        schoolImageView.setClickable(state);
        schoolTextView.setClickable(state);
        poeAI.setClickable(state);
        AIrelativeLayout.setClickable(state);
        AIview.setClickable(state);
        AIGIF.setClickable(state);
        contactUsButton.setClickable(state);
        shareAppButton.setClickable(state);
    }

    public void onSearchClick(View view) {
        //Animación del botón
        YoYo.with(Techniques.Pulse)
                .duration(300)
                .repeat(0)
                .playOn(view);
    }
}