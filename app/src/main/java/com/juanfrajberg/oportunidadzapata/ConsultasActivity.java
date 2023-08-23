package com.juanfrajberg.oportunidadzapata;

import android.app.Activity;
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
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.util.Random;

import pl.droidsonroids.gif.GifImageView;

public class ConsultasActivity extends AppCompatActivity {

    //Flecha para volver atrás
    static ImageView backArrow;

    //Pestaña de mensaje
    static EditText messageTabEditText;

    //Botón para enviar mensaje
    static ImageView sendMessageButton;

    //ScrollView de los mensajes
    static ScrollView messagesScrollView;

    //Para que no se adjunte un marginTop en el primer mensaje y se vea mal
    boolean firstTimeTalkingWithAI = true;

    //Para poder acceder desde la función de sendMessage y answerMessage
    View messagesToAdd;

    //Variable interna para saber si se está reproduciendo la animación de "pensar" y se puede enviar o no un mensaje
    boolean canSendMessage = true;

    //Para crear una respuesta aleatoria con el chat de AI
    int randomAnswer;

    //Para guardar el número de la última respuesta y que no se repita
    int lastAnswer;

    //Variable para saber si mostrar el Dialog al perderse la conexión
    boolean showWiFiStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Código básico para que se muestre la interfaz
        super.onCreate(savedInstanceState);
        setContentView(R.layout.consultas_activity);

        //Establecer el modo claro como predeterminado incluso con el modo oscuro (ya no es necesario, se configuró el modo oscuro en cada layout)
        //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        //Buscar elementos en layout
        backArrow = findViewById(R.id.consultas_arrow_imageview);

        messageTabEditText = findViewById(R.id.consultas_messagetab_edittext);

        sendMessageButton = findViewById(R.id.consultas_sendmessage_imageview);

        messagesScrollView = findViewById(R.id.consultas_messagesscrollview_scrollview);

        //Configurar que se vuelva atrás al hacer clic en la flecha
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //finish(); //No lo uso para que guarde el estado de la actividad
                startActivity(new Intent(ConsultasActivity.this, HomeActivity.class));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        //Pestaña de búsqueda
        messageTabEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE && Typewriter.hasFinished() && canSendMessage) {
                    messageTabEditText.clearFocus();
                    View view = getCurrentFocus();
                    sendMessage(view);
                } else {
                    messageTabEditText.clearFocus();
                }
                return false;
            }
        });

        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Typewriter.hasFinished() && canSendMessage) sendMessage(view);
            }
        });

        //Antes de que se envíen mensajes no debe aparecer el ScrollView porque tapa los otros elementos y no se los puede clickear
        ScrollView messagesScrollView = (ScrollView) findViewById(R.id.consultas_messagesscrollview_scrollview);
        messagesScrollView.setVisibility(View.GONE);

        //Esconder el teclado cuando cambia el focus
        //Hacerlo de esta forma es lo más óptimo porque permite que me desplace por el ScrollView
        messageTabEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        //finish(); //No lo uso para que guarde el estado de la actividad
        startActivity(new Intent(ConsultasActivity.this, HomeActivity.class));
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
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
                    i = new Intent(ConsultasActivity.this, WiFiActivity.class);
                    i.putExtra("State", "ON");
                    startActivity(i);
                    overridePendingTransition(R.anim.slidezoom_frombottom, R.anim.slidezoom_tobottom); //Animación
                }
            } else {
                //Toast.makeText(getApplicationContext(), "No hay conexión", Toast.LENGTH_SHORT).show();
                if (!WiFiInformation.isActivityVisible() && !WiFiInformation.lastState.equals("OFF") && WiFiInformation.isShowAgain() && showWiFiStatus) {
                    WiFiInformation.lastState = "OFF";
                    i = new Intent(ConsultasActivity.this, WiFiActivity.class);
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
        messageTabEditText.setClickable(state);
        sendMessageButton.setClickable(state);
        messagesScrollView.setClickable(state);
    }

    //Función que se llama desde el XML de este layout para animar los cuadros de más información sobre la AI
    public void animateMoreInfoLayouts(View view) {
        //Animación del botón
        YoYo.with(Techniques.Wave)
                .duration(450)
                .repeat(0)
                .playOn(view);
    }

    //Función para enviar mensajes y recibir respuesta de AI (todavía no configurada)
    public void sendMessage(View view) {
        canSendMessage = false;

        //Si el EditText no está vacío se ejecuta
        if (!TextUtils.isEmpty((messageTabEditText.getText()))) {
            //Animación del botón
            YoYo.with(Techniques.Pulse)
                    .duration(300)
                    .repeat(0)
                    .playOn(sendMessageButton);

            //Se comprueba si se está mostrando la introducción y ayuda sobre AI, si es así se esconde
            TextView explanationText = (TextView) findViewById(R.id.consultas_subtitle_textview);
            RelativeLayout firstItem = (RelativeLayout) findViewById(R.id.consultas_firstitem_relativelayout);
            RelativeLayout secondItem = (RelativeLayout) findViewById(R.id.consultas_seconditem_relativelayout);
            RelativeLayout thirdItem = (RelativeLayout) findViewById(R.id.consultas_thirditem_relativelayout);

            if (explanationText.isShown() || firstItem.isShown() || secondItem.isShown() || thirdItem.isShown()) {
                YoYo.with(Techniques.SlideOutRight)
                        .duration(500)
                        .repeat(0)
                        .playOn(explanationText);

                YoYo.with(Techniques.SlideOutLeft)
                        .duration(500)
                        .repeat(0)
                        .playOn(firstItem);

                YoYo.with(Techniques.SlideOutRight)
                        .duration(500)
                        .repeat(0)
                        .playOn(secondItem);

                YoYo.with(Techniques.SlideOutLeft)
                        .duration(500)
                        .repeat(0)
                        .playOn(thirdItem);

                //Se espera medio segundo (duración de la animación de salida) para esconder los elementos
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        explanationText.setVisibility(View.GONE);
                        firstItem.setVisibility(View.GONE);
                        secondItem.setVisibility(View.GONE);
                        thirdItem.setVisibility(View.GONE);
                    }
                }, 500);
            }

            //Se crea (infla) el layout con los mensajes del usuario y el bot
            LinearLayout AIElementsLayout = (LinearLayout) findViewById(R.id.consultas_messageslayout_linearlayout);
            messagesToAdd = getLayoutInflater().inflate(R.layout.messages_layout, AIElementsLayout, false);
            AIElementsLayout.addView(messagesToAdd);

            //Se le asigna el texto que escribimos
            TextView humanMessage = (TextView) messagesToAdd.findViewById(R.id.consultas_humanmessage_textview);
            humanMessage.setText(messageTabEditText.getText().toString());

            //Si es la primera vez hablando con la AI, el marginTop será de 0, sino queda un espacio feo en la parte superior
            if (firstTimeTalkingWithAI) {
                //Se muestra el ScrollView que había sido ocultado para que no tape otros elementos y se pueda hacer clic
                ScrollView messagesScrollView = (ScrollView) findViewById(R.id.consultas_messagesscrollview_scrollview);
                messagesScrollView.setVisibility(View.VISIBLE);

                RelativeLayout firstHumanMessage = (RelativeLayout) messagesToAdd.findViewById(R.id.consultas_humanmessage_relativelayout);
                LinearLayout.LayoutParams oldParams = (LinearLayout.LayoutParams) firstHumanMessage.getLayoutParams();
                LinearLayout.LayoutParams newParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                newParams.topMargin = 0; //Para que no haya espacio arriba
                newParams.gravity = Gravity.RIGHT; //Para que el mensaje aparezca a la derecha
                newParams.leftMargin = oldParams.leftMargin; //Consigo el marginLeft de los parámetros viejos, en los nuevos vale 0
                firstHumanMessage.setLayoutParams(newParams);

                //El valor de la variable se asigna a falso para que no se vuelva a ejecutar
                firstTimeTalkingWithAI = false;
            }

            //La animación FadeInUp también se ve muy bien
            YoYo.with(Techniques.SlideInUp)
                    .duration(500)
                    .repeat(0)
                    .playOn(messagesToAdd);

            //Responder mensaje
            answerMessage(messageTabEditText.getText().toString());

            //Limpiar el focus de este elemento al hacer clic en el botón de OK o terminado
            messageTabEditText.clearFocus();
            messageTabEditText.setText(null);
        } else {
            //Pierde el focus de todas formas
            messageTabEditText.clearFocus();
        }
    }

    //Función para responder los mensajes enviados por el usuario
    public void answerMessage(String message) {
        //Para que se entienda que no se pueden enviar mensajes mientras escribe el bot
        sendMessageButton.animate().alpha(0.35f).setDuration(600);

        final int min = 1500;
        final int max = 4500;
        //Genera un número aleatorio con el mínimo y el máximo inclusive
        final int randomWaitingTime = new Random().nextInt((max - min) + 1) + min;

        GifImageView answerAIGif = (GifImageView) messagesToAdd.findViewById(R.id.consultas_aithinkinggif_gifimageview);
        Handler thinkingTime = new Handler();
        thinkingTime.postDelayed(new Runnable() {
            public void run() {
                YoYo.with(Techniques.Tada)
                        .duration(400)
                        .repeat(0)
                        .playOn(answerAIGif);
                Handler hideGIF = new Handler();
                hideGIF.postDelayed(new Runnable() {
                    public void run() {
                        canSendMessage = true;
                        answerAIGif.setVisibility(View.GONE);

                        //Genera respuestas aleatorias hasta que no se repita con la anterior
                        generateRandomAnswer();
                        while (lastAnswer == randomAnswer) {
                            generateRandomAnswer();
                        }
                        lastAnswer = randomAnswer;

                        String firstAnswer = "¡Hola! Soy el Bot de Oportunidad Zapata, la aplicación ideal para la búsqueda y oferta de trabajo. 💼 Fui programado para asistirte en el proceso de crear tu currículum, y puedo resolver cualquier duda que tengas al respecto. 😃";
                        String secondAnswer = "¡Saludos! Soy el Chatbot de Oportunidad Zapata, la plataforma perfecta para encontrar y ofrecer empleos. 🌟 Estoy aquí para guiarte en la creación de tu currículum y puedo responder a todas tus preguntas sobre el tema. 📚";
                        String thirdAnswer = "¡Hola! Me llamo Bot de Oportunidad Zapata y estoy aquí para ayudarte en tu búsqueda y oferta de empleo. 🌼 Mi función es asistirte en la elaboración de tu currículum y puedo resolver cualquier consulta que tengas sobre este proceso. 📋";
                        String finalAnswer = "";
                        switch (randomAnswer) {
                            case 1:
                                finalAnswer = firstAnswer;
                                break;
                            case 2:
                                finalAnswer = secondAnswer;
                                break;
                            case 3:
                                finalAnswer = thirdAnswer;
                                break;
                        }

                        //Lo hace con el efecto Typewriter para que se vea mejor
                        Typewriter AIMessage = (Typewriter) messagesToAdd.findViewById(R.id.consultas_aimessage_textview);
                        AIMessage.setText("");
                        AIMessage.setCharacterDelay(35);
                        AIMessage.animateText(finalAnswer);
                    }
                }, 400); //El tiempo aleatorio que se demora en reproducir la animación de salida
            }
        }, randomWaitingTime); //El tiempo aleatorio que se demora en "pensar"

        message = message.toLowerCase(); //Convertir el mensaje en minúscula para que el reconocimiento sea más simple
        //if (message.contains("hola")) Toast.makeText(getApplicationContext(), "¡Hola!", Toast.LENGTH_SHORT).show(); //Para comprender mensajes en un futuro
    }

    public void generateRandomAnswer() {
        //El bot responde con un mensaje de forma aleatoria
        final int min = 1;
        final int max = 3;
        //Genera un número aleatorio con el mínimo y el máximo inclusive
        randomAnswer = new Random().nextInt((max - min) + 1) + min;
    }

    //Función para deslizar hasta abajo cuando la respuesta termina de ser generada
    public static void scrollToBottom() {
        //Se desliza el ScrollView automáticamente hasta abajo para ver el último mensaje
        //Si uso un fullScroll, como antes, se pierde el focus
        messagesScrollView.smoothScrollTo(0, messagesScrollView.getBottom());

        //Se desactiva el fondo más oscuro para indicar que ya se puede escribir
        sendMessageButton.animate().alpha(1).setDuration(600);
    }
}