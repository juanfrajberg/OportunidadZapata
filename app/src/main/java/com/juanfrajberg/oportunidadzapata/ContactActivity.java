package com.juanfrajberg.oportunidadzapata;

import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Locale;

public class ContactActivity extends AppCompatActivity {

    //Botones de las distintas pestañas
    static ImageView homeButton; //Botón de Casa
    static ImageView proposalButton; //Botón de Propuestas

    //Botones para contactarse
    //Primer ejemplo
    static ImageView firstExamplePhoneButton; //Teléfono
    static ImageView firstExampleWhatsAppButton; //WhatsApp
    //Segundo ejemplo
    static ImageView secondExamplePhoneButton; //Teléfono
    //Tercer ejemplo
    static ImageView thirdExamplePhoneButton; //Teléfono
    static ImageView thirdExampleWhatsAppButton; //WhatsApp

    //Opciones del HorizontalScrollView
    static TextView salud;
    static TextView tecnologia;
    static TextView finanzas;
    static TextView educacion;
    static TextView ventas;
    static TextView ingenieria;
    static TextView recursosHumanos;
    static TextView servicios;
    static TextView asesoríaLegal;
    static TextView artesYEntretenimiento;
    static TextView otros;

    //TextViews de las descripciones de los ejemplos
    static TextView firstExampleDescription;
    static TextView secondExampleDescription;
    static TextView thirdExampleDescription;

    //Layouts de las propuestas
    static RelativeLayout firstProposal;
    static RelativeLayout secondProposal;
    static RelativeLayout thirdProposal;

    //Botón para salir del modo de búsqueda
    static ImageView closeSearchImage;

    //Dialog en el que se muestra la información de cada persona
    Dialog infoDialog;

    //Variable para saber si mostrar el Dialog al perderse la conexión
    boolean showWiFiStatus;

    //Variables para indicar qué opción esta seleccionada
    boolean selectedSalud = false;
    boolean selectedTecnologia = false;
    boolean selectedFinanzas = false;
    boolean selectedEducacion = false;
    boolean selectedVentas = false;
    boolean selectedIngenieria = false;
    boolean selectedRecursosHumanos = false;
    boolean selectedServicios = false;
    boolean selectedAsesoriaLegal = false;
    boolean selectedArtesYEntretenimiento = false;
    boolean selectedOtros = false;

    //ScrollView donde están todas las propuestas
    private static ScrollView proposalsScrollView;

    //Variable para detectar si ya se ha seleccionado una opción del menú de filtrado
    boolean selectedOptionFiltering = false;

    //String usado para la filtración
    String selectedOptionString = "";

    //ArrayList que guarda el ID de las propuestas para hacer más cómoda la filtración
    private ArrayList<Integer> proposalsIDs = new ArrayList<Integer>();

    //Variable para saber cuántos elementos se borraron y poder borrar los elementos correctos al seleccionar una opción
    int eliminatedElements = 0;

    //Variable que se recibe de HomeActivity en el EditText de búsqueda
    String valueToSearch;

    //Variable que guarda la cantidad de elementos que se encontraron al usar la búsqueda en HomeActivity
    //Para un futuro, estaría bueno que esto funcione también al seleccionar distintas categorías y que diga cuántos resultados encuentran
    //Pero, para hacer eso hay que cambiar una parte importante, por eso el Toast aclara que la búsqueda es en total
    int elementsFoundSearch = 0;

    boolean alreadyShowedToastElementsFound = false;

    //Variable para saber si hay que borrar o no el resaltado de la búsqueda
    boolean deleteSpan = false;

    //Variable que guarda la posición del ScrollView para cuando se borran los resultados de la búsqueda
    int scrollYPosition = 0;

    //Bundle donde se consigue la información de si es modo de búsqueda o no
    Bundle bundleFromHomeActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Código básico para que se muestre la interfaz
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_activity);

        //Establecer el modo claro como predeterminado incluso con el modo oscuro (ya no es necesario, se configuró el modo oscuro en cada layout)
        //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        //Buscar elementos en layout
        homeButton = findViewById(R.id.contact_home_imageview);
        proposalButton = findViewById(R.id.contact_proposal_imageview);
        proposalsScrollView = findViewById(R.id.contact_layoutscroll_scrollview);

        //Nota: ninguno de estos botones se usan, pero pueden servir en un futuro
        //La idea era usarlos para enviar un mensaje de WhatsApp o llamar, pero es mucho
        //más simple con un onClick desde su XML
        firstExamplePhoneButton = findViewById(R.id.contact_firstexamplephone_imageview);
        firstExampleWhatsAppButton = findViewById(R.id.contact_firstexamplewhatsapp_imageview);
        secondExamplePhoneButton = findViewById(R.id.contact_secondexamplephone_imageview);
        thirdExamplePhoneButton = findViewById(R.id.contact_thirdexamplephone_imageview);
        thirdExampleWhatsAppButton = findViewById(R.id.contact_thirdexamplewhatsapp_imageview);

        salud = findViewById(R.id.contact_salud_textview);
        tecnologia = findViewById(R.id.contact_tecnologia_textview);
        finanzas = findViewById(R.id.contact_finanzas_textview);
        educacion = findViewById(R.id.contact_educacion_textview);
        ventas = findViewById(R.id.contact_ventas_textview);
        ingenieria = findViewById(R.id.contact_ingenieria_textview);
        recursosHumanos = findViewById(R.id.contact_recursoshumanos_textview);
        servicios = findViewById(R.id.contact_servicios_textview);
        asesoríaLegal = findViewById(R.id.contact_asesorialegal_textview);
        artesYEntretenimiento = findViewById(R.id.contact_artesyentretenimiento_textview);
        otros = findViewById(R.id.contact_otros_textview);

        firstExampleDescription = findViewById(R.id.contact_firstexampledescriptionrl_textview);
        secondExampleDescription = findViewById(R.id.contact_secondexampledescriptionrl_textview);
        thirdExampleDescription = findViewById(R.id.contact_thirdexampledescriptionrl_textview);

        firstProposal = findViewById(R.id.contact_firstexampleview_relativelayout);
        secondProposal = findViewById(R.id.contact_secondexampleview_relativelayout);
        thirdProposal = findViewById(R.id.contact_thirdexampleview_relativelayout);

        closeSearchImage = findViewById(R.id.contact_closesearch_imageview);

        //Se detecta si se está o no en el modo de búsqueda
        bundleFromHomeActivity = getIntent().getExtras();
        if (!bundleFromHomeActivity.getString("searchText").isEmpty()) {
            closeSearchImage.setVisibility(View.VISIBLE);
        }
        else {
            closeSearchImage.setVisibility(View.GONE);
        }

        View backgroundView = findViewById(R.id.contact_backgroundanimation_view);
        backgroundView.setVisibility(View.GONE);

        closeSearchImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Para que el ScrollView se deslice de manera fluida al inicio
                try {
                    ObjectAnimator.ofInt(proposalsScrollView, "scrollY",  0).setDuration(1500).start();
                } catch (Exception e) {
                    proposalsScrollView.smoothScrollTo(0, scrollYPosition);
                }

                //Animación del elemento
                YoYo.with(Techniques.Pulse)
                        .duration(450)
                        .repeat(0)
                        .playOn(closeSearchImage);

                backgroundView.setVisibility(View.VISIBLE);
                //Blurry.with(getApplicationContext()).radius(25).sampling(2).onto(backgroundView);
                backgroundView.setAlpha(0f);
                backgroundView.animate().alpha(1f).setDuration(1500);

                Handler waitUntilAnimationIsFinished = new Handler();
                waitUntilAnimationIsFinished.postDelayed(new Runnable() {
                    public void run() {
                        //Animación del elemento
                        YoYo.with(Techniques.FadeOut)
                                .duration(450)
                                .repeat(0)
                                .playOn(closeSearchImage);

                        Handler waitForScrollViewToGoUp = new Handler();
                        waitForScrollViewToGoUp.postDelayed(new Runnable() {
                            public void run() {
                                backgroundView.animate().alpha(0f).setDuration(1500);
                                createAllProposals("FirstTime");

                                Handler hideAfterEverything = new Handler();
                                hideAfterEverything.postDelayed(new Runnable() {
                                    public void run() {
                                        backgroundView.setVisibility(View.GONE);
                                    }
                                }, 1500);
                            }
                        }, 1050); //El tiempo que tarda la animación del ScrollView en subir
                    }
                }, 450); //El tiempo que se demora la animación del botón

                scrollYPosition = proposalsScrollView.getScrollY();
                deleteSpan = true;
            }
        });

        //Abrir la pestaña de DataActivity
        firstProposal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Animación del elemento
                YoYo.with(Techniques.Pulse)
                        .duration(450)
                        .repeat(0)
                        .playOn(firstProposal);
                openInfo(0, 1, "", "", "", "", "", "", "", "", "", "", "", "");
            }
        });

        secondProposal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Animación del elemento
                YoYo.with(Techniques.Pulse)
                        .duration(450)
                        .repeat(0)
                        .playOn(secondProposal);
                openInfo(0, 2, "", "", "", "", "", "", "", "", "", "", "", "");
            }
        });

        thirdProposal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Animación del elemento
                YoYo.with(Techniques.Pulse)
                        .duration(450)
                        .repeat(0)
                        .playOn(thirdProposal);
                openInfo(0, 3, "", "", "", "", "", "", "", "", "", "", "", "");
            }
        });

        //Abrir las distintas pestañas
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ContactActivity.this, HomeActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left); //Animación

                //Animación del botón
                YoYo.with(Techniques.Pulse)
                        .duration(450)
                        .repeat(0)
                        .playOn(homeButton);
            }
        });

        proposalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ContactActivity.this, SendProposalActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left); //Animación

                //Animación del botón
                YoYo.with(Techniques.Pulse)
                        .duration(450)
                        .repeat(0)
                        .playOn(proposalButton);
            }
        });

        //Al final del onCreate(), se crean todas las propuestas indicando que es la primera vez
        createAllProposals("FirstTime");
    }

    //Función para resaltar el texto recibido de HomeActivity
    public void highlightText(TextView textView, String textToHighlight, boolean closeActionPerformed) {
        String textFromTextView = textView.getText().toString().toLowerCase(Locale.ROOT);
        int a = textFromTextView.indexOf(textToHighlight, 0);
        Spannable wordToSpan = new SpannableString(textView.getText());
        
        for (int i = 0; i < textFromTextView.length() && a != -1; i = a + 1) {
            a = textFromTextView.indexOf(textToHighlight, i);
            if (a == -1)
                break;
            else {
                if (!closeActionPerformed) {
                    //Resaltar texto con amarillo de fondo y negro de texto
                    wordToSpan.setSpan(new BackgroundColorSpan(0xFFFFFF00), a, a + textToHighlight.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    wordToSpan.setSpan(new ForegroundColorSpan(Color.parseColor("#000000")), a, a + textToHighlight.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    textView.setText(wordToSpan, TextView.BufferType.SPANNABLE);

                    //Se incrementa el número de elementos encontrados para el Toast que dice cuántos hay
                    elementsFoundSearch++;
                }
                else {
                    //Resaltar texto con amarillo de fondo y negro de texto
                    SpannableString ss=(SpannableString)textView.getText();
                    ForegroundColorSpan[] spans=ss.getSpans(0, textView.getText().length(), ForegroundColorSpan.class);
                    for(int j=0; j<spans.length; j++){
                        ss.removeSpan(spans[j]);
                    }
                }
            }
        }
    }

    //Función para abrir el Dialog con más información de la persona seleccionada
    //En un futuro hay que añadir parámetros para que apenas abra tenga el nombre, mail y demás datos
    private void openInfo(int id, int numberInfo, String job, String name, String student, String timeDay, String timeMonth, String timeYear, String email, String phone, String course, String division, String description, String showStudent) {
        infoDialog = new Dialog(ContactActivity.this);
        infoDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        infoDialog.getWindow().getAttributes().windowAnimations = R.style.InfoDialogAnimation;
        infoDialog.setCancelable(true);

        //Switch para saber qué información mostrar
        switch (numberInfo) { //No hay un caso default porque realmente no es necesario
            case 0: //Se abre un Dialog con información de una persona real, no de ejemplo
                infoDialog.setContentView(R.layout.info_dialog);
                TextView jobTextView = (TextView) infoDialog.findViewById(R.id.info_job_textview);
                jobTextView.setText(job);
                TextView nameTextView = (TextView) infoDialog.findViewById(R.id.info_personname_textview);
                nameTextView.setText(name);
                TextView studentTextView = (TextView) infoDialog.findViewById(R.id.info_alumnoquelorecomienda_textview);
                if (showStudent.equals("true")) {
                    studentTextView.setText("Recomendado/a por " + student + " de " + course.substring(0, 1) + "° " + division.substring(0, 1) + "°");
                } else {
                    studentTextView.setText("Prefiere ocultar el nombre del alumno/a que lo/la recomienda");
                }
                TextView dateTextView = (TextView) infoDialog.findViewById(R.id.info_time_textview);
                dateTextView.setText("Publicado el " + timeDay + "/" + timeMonth + "/" + timeYear);
                TextView mailTextView = (TextView) infoDialog.findViewById(R.id.info_personmail_textview);
                mailTextView.setText(email);
                TextView phoneTextView = (TextView) infoDialog.findViewById(R.id.info_whatsappnumber_textview);
                phoneTextView.setText("+54 9 " + phone.substring(0, 3) + " " + phone.substring(3, 6) + "-" + phone.substring(6, phone.length()));
                TextView descriptionTextView = (TextView) infoDialog.findViewById(R.id.info_description_textview);
                descriptionTextView.setText(description);
                //int randomDescription = new Random().nextInt(3 - 1 + 1) + 1;
                /*
                int randomDescription = id % 3;
                //Log.d("OZ", "ID -> " + id + "\nDescription -> " + randomDescription);
                switch (randomDescription) {
                    case 0:
                        descriptionTextView.setText("En mi trabajo como " + job.toLowerCase() + ", mi enfoque principal es cumplir con metas y objetivos. Utilizo mis habilidades y conocimientos para aportar valor en mi área. Trabajo en equipo y adopto la tecnología para optimizar procesos. Siempre estoy abierto a aprender y crecer, sin importar el tipo de trabajo que desempeñe.");
                        break;
                    case 1:
                        descriptionTextView.setText("Mi puesto como " + job.toLowerCase() + " se centra en alcanzar metas y lograr resultados efectivos. Empleo mis habilidades y experiencia para contribuir al éxito en mi campo laboral. Colaboro con colegas y aprovecho la tecnología para mejorar procesos. Siempre estoy dispuesto a aprender y crecer, sin importar el contexto laboral en el que me encuentre.");
                        break;
                    case 2:
                        descriptionTextView.setText("En mi profesión (" + job.toLowerCase() + "), me dedico a resolver problemas y lograr resultados efectivos. Utilizo mis habilidades y experiencia para contribuir al éxito en mi campo. Colaboro con equipos y aprovecho la tecnología para mejorar procesos. Siempre estoy dispuesto a aprender y crecer en cualquier entorno laboral en el que me encuentre.");
                        break;
                }
                break;
                 */
                break;
            case 1:
                infoDialog.setContentView(R.layout.firstinfo_dialog);
                break;
            case 2:
                infoDialog.setContentView(R.layout.secondinfo_dialog);
                break;
            case 3:
                infoDialog.setContentView(R.layout.thirdinfo_dialog);
                break;
        }

        //Cerrar el Dialog al hacer clic en al cruz
        ImageView closeDialogImage = (ImageView) infoDialog.findViewById(R.id.info_close_imageview);
        closeDialogImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Animación del botón
                YoYo.with(Techniques.Pulse)
                        .duration(450)
                        .repeat(0)
                        .playOn(closeDialogImage);
                infoDialog.dismiss();
            }
        });

        //Abrir WhatsApp al hacer clic en el texto e imagen de WhatsApp
        ImageView whatsAppImage = (ImageView) infoDialog.findViewById(R.id.info_whatsappicon_imageview);
        whatsAppImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Animación del botón
                YoYo.with(Techniques.RubberBand)
                        .duration(450)
                        .repeat(0)
                        .playOn(whatsAppImage);
                openWhatsAppFromInfoDialog();
            }
        });
        TextView whatsAppText = (TextView) infoDialog.findViewById(R.id.info_whatsappnumber_textview);
        whatsAppText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Animación del botón
                YoYo.with(Techniques.RubberBand)
                        .duration(450)
                        .repeat(0)
                        .playOn(whatsAppText);
                openWhatsAppFromInfoDialog();
            }
        });

        //Abrir Instagram al hacer clic en el texto e imagen de Instagram
        //Se ejecuta en un try porque no todos publican su usuario de Instagram, es opcional
        try {
            ImageView instagramImage = (ImageView) infoDialog.findViewById(R.id.info_instagramicon_imageview);
            instagramImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Animación del botón
                    YoYo.with(Techniques.RubberBand)
                            .duration(450)
                            .repeat(0)
                            .playOn(instagramImage);
                    openInstagramFromInfoDialog();
                }
            });
            TextView instagramText = (TextView) infoDialog.findViewById(R.id.info_instagramusername_textview);
            instagramText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Animación del botón
                    YoYo.with(Techniques.RubberBand)
                            .duration(450)
                            .repeat(0)
                            .playOn(instagramText);
                    openInstagramFromInfoDialog();
                }
            });
        } catch (Exception e) {
        }

        infoDialog.show();

        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.92); //Un 92% de la pantalla para la anchura
        infoDialog.getWindow().setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT); //El alto se ajusta al contenido del Dialog
        infoDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    //Función para abrir WhatsApp (más adelante habrá que actualizarla, y buscar el número desde la base de datos)
    public void openWhatsApp(View view) {
        //Animación del botón
        YoYo.with(Techniques.Bounce)
                .duration(450)
                .repeat(0)
                .playOn(view);
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone=542616330460&text=%C2%A1Hola!%20Este%20es%20un%20mensaje%20de%20prueba%20al%20creador%20de%20la%20aplicaci%C3%B3n%2C%20gracias%20por%20usarla%20%F0%9F%98%81")));
    }

    //Función para marcar el teléfono (no llamar, solamente se abre la app de teléfono y se ingresa el número. Más adelante habrá que actualizarla, y buscar el número desde la base de datos)
    public void openPhone(View view) {
        //Animación del botón
        YoYo.with(Techniques.Bounce)
                .duration(450)
                .repeat(0)
                .playOn(view);
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:2616330460"));
        startActivity(intent);
    }

    //Para que haya una animación cuando se presiona el botón atrás, y no sea la que aparece
    //por defecto
    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
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

                    if (infoDialog != null) infoDialog.dismiss();

                    i = new Intent(ContactActivity.this, WiFiActivity.class);
                    i.putExtra("State", "ON");
                    startActivity(i);
                    overridePendingTransition(R.anim.slidezoom_frombottom, R.anim.slidezoom_tobottom); //Animación
                }
            } else {
                //Toast.makeText(getApplicationContext(), "No hay conexión", Toast.LENGTH_SHORT).show();
                if (!WiFiInformation.isActivityVisible() && !WiFiInformation.lastState.equals("OFF") && WiFiInformation.isShowAgain() && showWiFiStatus) {
                    WiFiInformation.lastState = "OFF";

                    if (infoDialog != null) infoDialog.dismiss();

                    i = new Intent(ContactActivity.this, WiFiActivity.class);
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
        homeButton.setClickable(state);
        proposalButton.setClickable(state);
        firstExamplePhoneButton.setClickable(state);
        firstExampleWhatsAppButton.setClickable(state);
        secondExamplePhoneButton.setClickable(state);
        thirdExamplePhoneButton.setClickable(state);
        thirdExampleWhatsAppButton.setClickable(state);
        salud.setClickable(state);
        tecnologia.setClickable(state);
        finanzas.setClickable(state);
        educacion.setClickable(state);
        ventas.setClickable(state);
        ingenieria.setClickable(state);
        recursosHumanos.setClickable(state);
        servicios.setClickable(state);
        asesoríaLegal.setClickable(state);
        artesYEntretenimiento.setClickable(state);
        otros.setClickable(state);
        firstExampleDescription.setClickable(state);
        secondExampleDescription.setClickable(state);
        thirdExampleDescription.setClickable(state);
        firstProposal.setClickable(state);
        secondProposal.setClickable(state);
        thirdProposal.setClickable(state);
        proposalsScrollView.setClickable(state);
    }

    //Función para abrir el chat de WhatsApp desde el Dialog con más información sobre una persona a contactar
    public void openWhatsAppFromInfoDialog() {
        //Obtenemos el número de WhatsApp tal cual está escrito en el Dialog
        TextView whatsAppNumber = (TextView) infoDialog.findViewById(R.id.info_whatsappnumber_textview);
        String whatsAppNumberString = whatsAppNumber.getText().toString();

        //Quitamos los espacios en blanco y el guión del número de teléfono
        whatsAppNumberString = whatsAppNumberString.replaceAll("\\s+", "");
        whatsAppNumberString = whatsAppNumberString.replaceAll("\\W+", "");
        //Quitamos el 9 que continúa de +54
        int i = 2;
        whatsAppNumberString = whatsAppNumberString.substring(0, i) + whatsAppNumberString.substring(i + 1);

        //Abrimos el chat con el número del Dialog
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone=+" + whatsAppNumberString + "&text=Hola%2C%20¿cómo%20estás%3F%20Te%20contacto%20porque%20me%20gustaría%20obtener%20más%20información%20sobre%20los%20servicios%20que%20ofrecés%2C%20muchas%20gracias%20☺")));
    }

    //Función para abrir el chat de WhatsApp desde el Dialog con más información sobre una persona a contactar
    public void openInstagramFromInfoDialog() {
        //Obtenemos el usuario de Instagram tal cual está escrito en el Dialog
        TextView instagramUserName = (TextView) infoDialog.findViewById(R.id.info_instagramusername_textview);
        String instagramUserNameString = instagramUserName.getText().toString();

        //Eliminamos el @ (arroba) del String
        instagramUserNameString = instagramUserNameString.replace("@", "");

        //Abrimos Instagram con el perfil del Dialog
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://instagram.com/" + instagramUserNameString)));
    }

    private void createProposals(int id, String name, String phone, String time, String email, String job, String student, String course, String division, String descriptionShort, String descriptionFormal, String showStudent, String category, String socialMedia, String username) {
        //Se crea (infla) el layout con las propuestas
        LinearLayout inflatedProposals = (LinearLayout) findViewById(R.id.contact_inflatedproposals_linearlayout);
        View proposalToAdd = getLayoutInflater().inflate(R.layout.proposal_layout, inflatedProposals, false);

        /*try {
            inflatedProposals.addView(proposalToAdd, id - 1);
        } catch (Exception e) {
            Log.e("OZ", "" + e);
            inflatedProposals.addView(proposalToAdd);
        }
         */
        inflatedProposals.addView(proposalToAdd);

        //TextView idTextView = (TextView) proposalToAdd.findViewById(R.id.proposal_id_textview);
        //idTextView.setText(id);

        //Para convertir el formato de la variable "time" a texto
        time = time.substring(0, 10);
        time = time.replace("-", "");
        //Log.d("OZ", time);
        String timeYear = time.substring(0, 4);
        String timeMonth = time.substring(4, 6);
        timeMonth = timeMonth.replaceFirst("^0+(?!$)", "");
        String timeMonthInNumbers = timeMonth;
        //Log.d("OZ", timeMonth);
        String timeDay = time.substring(6, 8);
        timeDay = timeDay.replaceFirst("^0+(?!$)", "");
        //Log.d("OZ", timeDay);

        //Para el mes se deben usar las siguientes líneas de código
        DateTimeFormatter fmt = new DateTimeFormatterBuilder().parseCaseInsensitive().appendPattern("MMMM").toFormatter(new Locale("es", "ES"));
        timeMonth = Month.of(Integer.parseInt(timeMonth)).getDisplayName(TextStyle.FULL_STANDALONE, new Locale("es", "ES"));
        timeMonth = timeMonth.substring(0, 1).toUpperCase() + timeMonth.substring(1).toLowerCase();

        //Se les asigna el valor a los TextView
        TextView dateProposal = (TextView) proposalToAdd.findViewById(R.id.proposal_time_textview);
        dateProposal.setText(timeDay + " de " + timeMonth + " del " + timeYear);

        TextView jobProposal = (TextView) proposalToAdd.findViewById(R.id.proposal_titlerl_textview);
        jobProposal.setText(job);

        TextView nameProposal = (TextView) proposalToAdd.findViewById(R.id.proposal_subtitlerl_textview);
        nameProposal.setText(name);

        TextView descriptionShortProposal = (TextView) proposalToAdd.findViewById(R.id.proposal_descriptionrl_textview);
        descriptionShortProposal.setText(Html.fromHtml(descriptionShort + " <font color='#3876F6'><u>Leer más.</u></font>"));

        //Se consigue (si se envió) el texto de búsqueda de HomeActivity
        if (bundleFromHomeActivity != null && !deleteSpan) {
            valueToSearch = bundleFromHomeActivity.getString("searchText");
            highlightText(jobProposal, valueToSearch, false);
            highlightText(nameProposal, valueToSearch, false);
            highlightText(descriptionShortProposal, valueToSearch, false);
            highlightText(dateProposal, valueToSearch, false);
        }
        else {
            try {
                valueToSearch = bundleFromHomeActivity.getString("searchText");
                highlightText(jobProposal, valueToSearch, true);
                highlightText(nameProposal, valueToSearch, true);
                highlightText(descriptionShortProposal, valueToSearch, true);
                highlightText(dateProposal, valueToSearch, true);
            } catch (Exception e) {
                //No se ha usado la función de búsqueda si este "error" se produce
            }
        }

        //Descripción para aquellas propuestas que fueron completadas con el formulario de Google inicial y no cuentan con el dato
        /*
        final int randomYears = new Random().nextInt(15) + 2;
        descriptionProposal.setText(Html.fromHtml("Me desempeño como " + job.toLowerCase() + " hace más de " + randomYears + " años. Fui recomendado/a por " + student + ". " + "<font color='#3876F6'><u>Leer más.</u></font>"));
         */

        //Abrir el Dialog de más información
        RelativeLayout proposalLayout = proposalToAdd.findViewById(R.id.proposal_view_relativelayout);
        String finalTimeDay = timeDay;
        proposalLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Log.d("OZ", "" + descriptionFormal);
                openInfo(id, 0, job, name, student, finalTimeDay, timeMonthInNumbers, timeYear, email, phone, course, division, descriptionFormal, showStudent);
            }
        });

        //Se programa el onClick del teléfono y WhatsApp
        ImageView phoneButton = (ImageView) proposalToAdd.findViewById(R.id.proposal_phone_imageview);
        phoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Animación del botón
                YoYo.with(Techniques.Bounce)
                        .duration(450)
                        .repeat(0)
                        .playOn(phoneButton);
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + phone));
                startActivity(intent);
            }
        });

        ImageView whatsAppButton = (ImageView) proposalToAdd.findViewById(R.id.proposal_whatsapp_imageview);
        whatsAppButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Animación del botón
                YoYo.with(Techniques.Bounce)
                        .duration(450)
                        .repeat(0)
                        .playOn(whatsAppButton);

                //Obtenemos el número de WhatsApp tal cual está escrito en el Dialog

                //Quitamos los espacios en blanco y el guión del número de teléfono
                String WhatsAppPhone = phone;

                //Abrimos el chat con el número del Dialog
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone=+" + WhatsAppPhone + "&text=Hola%2C%20buenas%20tardes!%20Me%20contacto%20cone%20usted%20porque%20me%20interesan%20los%20servicios%20que%20ofrece%20en%20Oportunidad%20Zapata%20😁")));
            }
        });
    }

    //Función para crear las propuestas (también cuando se llaman desde una categoría=
    public void createAllProposals(String categoryFromFunction) {
        //Se usa más adelante para decidir qué propuestas borrar al seleccionar una categoría
        eliminatedElements = 0;

        LinearLayout inflatedProposals = (LinearLayout) findViewById(R.id.contact_inflatedproposals_linearlayout);
        inflatedProposals.removeAllViews();

        //Se accede a la información guardada en la base de datos
        DatabaseReference databaseReference;
        databaseReference = FirebaseDatabase.getInstance().getReference("1JcKn4lV9YC5cF8o_QyekJ7-72u-bRn748CLrLc9jTD0/workers");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //Para saber la cantidad de hijos (largo de la lista)
                int size = (int) snapshot.getChildrenCount();
                //Log.d("OZ", "Size -> " + size);

                //Se crean los Strings que guardarán los datos
                String name = "";
                String phone = "";
                String time = "";
                String email = "";
                String job = "";
                String descriptionShort = "";
                String descriptionFormal = "";
                String student = "";
                String course = "";
                String division = "";
                String showStudent = "";
                String category = "";
                String socialMedia = "";
                String username = "";

                //Se le asigna el valor a estos Strings
                for (int i = 1; i < size + 1; i++) {
                    name = snapshot.child(String.valueOf(i)).child("fullname").getValue(String.class);
                    phone = String.valueOf(snapshot.child(String.valueOf(i)).child("phone").getValue(Long.class));
                    time = snapshot.child(String.valueOf(i)).child("time").getValue(String.class);
                    email = snapshot.child(String.valueOf(i)).child("email").getValue(String.class);
                    job = snapshot.child(String.valueOf(i)).child("job").getValue(String.class);
                    //Todavía no hay una descripción de cada trabajador en la base de datos (arreglar en un futuro)
                    //description = snapshot.child(String.valueOf(i)).child("description").getValue(String.class);
                    student = snapshot.child(String.valueOf(i)).child("student").getValue(String.class);
                    course = snapshot.child(String.valueOf(i)).child("course").getValue(String.class);
                    division = snapshot.child(String.valueOf(i)).child("division").getValue(String.class);
                    descriptionShort = snapshot.child(String.valueOf(i)).child("descriptionShort").getValue(String.class);
                    descriptionFormal = snapshot.child(String.valueOf(i)).child("descriptionFormal").getValue(String.class);
                    showStudent = snapshot.child(String.valueOf(i)).child("showStudent").getValue(String.class);
                    category = snapshot.child(String.valueOf(i)).child("category").getValue(String.class);
                    socialMedia = snapshot.child(String.valueOf(i)).child("socialMedia").getValue(String.class);
                    username = snapshot.child(String.valueOf(i)).child("username").getValue(String.class);

                    //Se crean las propuestas con la información dada
                    //Solucionar error: FirstTime y All equivalen a lo mismo
                    if (categoryFromFunction.equals("FirstTime")) {
                        createProposals(i, name, phone, time, email, job, student, course, division, descriptionShort, descriptionFormal, showStudent, category, socialMedia, username);
                        proposalsIDs.add(i);
                    } else if (categoryFromFunction.equals("All")) {
                        /*
                        if (!proposalsIDs.contains(i)) {
                            createProposals(i, name, phone, time, email, job, student, course, division, descriptionShort, descriptionFormal, showStudent, category, socialMedia, username);
                            proposalsIDs.add(i);
                        }
                         */
                        createProposals(i, name, phone, time, email, job, student, course, division, descriptionShort, descriptionFormal, showStudent, category, socialMedia, username);
                        proposalsIDs.add(i);
                    } else if (category.equals(categoryFromFunction)) {
                        //Log.d("OZ", "Remove -> " + (i));
                        /*
                        createProposals(i, name, phone, time, email, job, student, course, division, descriptionShort, descriptionFormal, showStudent, category, socialMedia, username);
                        proposalsIDs.add(i);
                    } else {
                        try {
                            eliminatedElements++;
                            inflatedProposals.removeViewAt(i - eliminatedElements);
                        } catch (Exception e) {
                            //Log.e("OZ", "" + e);
                        }
                        //Log.d("OZ", "" + proposalsIDs);
                        proposalsIDs.remove(Integer.valueOf(i));
                        //Log.d("OZ", "" + proposalsIDs);
                         */

                        //Log.d("OZ", "Here");
                        createProposals(i, name, phone, time, email, job, student, course, division, descriptionShort, descriptionFormal, showStudent, category, socialMedia, username);
                        proposalsIDs.add(i);
                    }
                }

                    /*
                    } else if (category.equals(categoryFromFunction)) {
                        createProposals(i, name, phone, time, email, job, student, course, division, descriptionShort, descriptionFormal, showStudent, category, socialMedia, username);
                        proposalsIDs.add(i);

                        //Log.d("OZ", "" + proposalsIDs);
                    }

                    //Log.d("OZ", "Category " + i + " -> " + category + ".");
                }

                     */

                if (bundleFromHomeActivity != null) {
                    showToastElementsFound();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "¡No se pudo acceder a las demás propuestas, revisá tu conexión!", Toast.LENGTH_LONG);
            }
        });

        //Para ir a la posición donde estabámos
        //Pero primero, se detecta si se está o no en el modo de búsqueda y se ha hecho clic en el botón
        bundleFromHomeActivity = getIntent().getExtras();
        Log.d("OZ", deleteSpan + "");
        if (!bundleFromHomeActivity.getString("searchText").isEmpty() && deleteSpan) {
            Log.d("OZ", "Executed :)");
            Handler waitUntilProposalsAreCreated = new Handler();
            waitUntilProposalsAreCreated.postDelayed(new Runnable() {
                public void run() {
                    //Para que el ScrollView se deslice de manera fluida a la posición donde estábamos antes de borrar lo resaltado
                    try {
                        ObjectAnimator.ofInt(proposalsScrollView, "scrollY", scrollYPosition).setDuration(1500).start();
                    } catch (Exception e) {
                        proposalsScrollView.smoothScrollTo(0, scrollYPosition);
                    }
                }
            }, 0);
        }
    }

    //Función que se llama al seleccionar una categoría
    public void selectedOption(View optionSelected) {
        //Animación del elemento
        YoYo.with(Techniques.FlipInX)
                .duration(450)
                .repeat(0)
                .playOn(optionSelected);

        //Se pone en negrita la categoría seleccionada y las demás se deseleccionan
        LinearLayout optionsLayout = (LinearLayout) findViewById(R.id.contact_optionslayout_linearlayout);
        int numberOptions = optionsLayout.getChildCount();
        for (int i = 0; i < numberOptions; i++) {
            TextView option = (TextView) optionsLayout.getChildAt(i);
            option.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
        }

        if (optionSelected instanceof TextView) {
            TextView optionSelectedTextView = (TextView) optionSelected;
            if (!selectedOptionFiltering) {
                LinearLayout inflatedProposals = (LinearLayout) findViewById(R.id.contact_inflatedproposals_linearlayout);
                //inflatedProposals.removeAllViews();
                Log.d("TRASH", "DELETE ALL!");
                createAllProposals(optionSelectedTextView.getText().toString());
                optionSelectedTextView.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_extrabold));
                selectedOptionString = optionSelectedTextView.getText().toString();
                selectedOptionFiltering = true;
                //Log.d("OZ", "1");
                //Log.d("OZ", "Opción -> " + selectedOptionString + ".");
            } else {
                if (selectedOptionString.equals(optionSelectedTextView.getText().toString())) {
                    optionSelectedTextView.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
                    createAllProposals("All");
                    Log.d("SUN", "MAKE ALL!");
                    selectedOptionString = optionSelectedTextView.getText().toString();
                    //Log.d("OZ", "2");
                    selectedOptionFiltering = false;
                } else {
                    optionSelectedTextView.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_extrabold));
                    LinearLayout inflatedProposals = (LinearLayout) findViewById(R.id.contact_inflatedproposals_linearlayout);
                    //inflatedProposals.removeAllViews();
                    Log.d("TRASH", "DELETE ALL!");
                    createAllProposals(optionSelectedTextView.getText().toString());
                    selectedOptionString = optionSelectedTextView.getText().toString();
                    //Log.d("OZ", "3");
                    selectedOptionFiltering = true;
                }
            }

            //Log.d("OZ", "Variable -> " + selectedOptionString + "\nReal -> " + optionSelectedTextView.getText().toString());
        }

        //Log.d("OZ", "" + selectedOptionFiltering);
    }

    //Función que se llama cuando desde HomeActivity se hizo una búsqueda para imprimir la cantidad de resultados encontrados
    public void showToastElementsFound() {
        if (!alreadyShowedToastElementsFound) {
            if (elementsFoundSearch != 0) {
                if (elementsFoundSearch == 1) {
                    Toast.makeText(getApplicationContext(), "Se encontró un resultado en total.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Se encontraron " + elementsFoundSearch + " resultados en total.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "No se encontraron resultados.", Toast.LENGTH_SHORT).show();
            }
        }

        alreadyShowedToastElementsFound = true;
    }
}