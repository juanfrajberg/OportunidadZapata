package com.juanfrajberg.oportunidadzapata;

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
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import java.util.Locale;
import java.util.Random;

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

    //Dialog en el que se muestra la información de cada persona
    Dialog infoDialog;

    //Variable para saber si mostrar el Dialog al perderse la conexión
    boolean showWiFiStatus;

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

        //Hacer que las descripciones se puedan scrollear
        //Nota: lo reemplacé por NestedScrollView, este método no funciona cuando los textos
        //están ubicados en un ScrollView
        //firstExampleDescription.setMovementMethod(new ScrollingMovementMethod());
        //secondExampleDescription.setMovementMethod(new ScrollingMovementMethod());
        //thirdExampleDescription.setMovementMethod(new ScrollingMovementMethod());

        //"Seleccionar" el ítem, cambiando su tipo de fuente y el de las demás
        //Nota: es un verdadero lío, buscar otra solución en el futuro por favor
        salud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Animación del texto
                YoYo.with(Techniques.FlipInX)
                        .duration(450)
                        .repeat(0)
                        .playOn(salud);

                salud.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_extrabold));
                tecnologia.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
                finanzas.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
                educacion.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
                ventas.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
                ingenieria.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
                recursosHumanos.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
                servicios.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
                asesoríaLegal.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
                artesYEntretenimiento.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
                otros.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
            }
        });

        tecnologia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Animación del texto
                YoYo.with(Techniques.FlipInX)
                        .duration(450)
                        .repeat(0)
                        .playOn(tecnologia);

                salud.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
                tecnologia.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_extrabold));
                finanzas.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
                educacion.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
                ventas.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
                ingenieria.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
                recursosHumanos.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
                servicios.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
                asesoríaLegal.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
                artesYEntretenimiento.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
                otros.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
            }
        });

        finanzas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Animación del texto
                YoYo.with(Techniques.FlipInX)
                        .duration(450)
                        .repeat(0)
                        .playOn(finanzas);

                salud.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
                tecnologia.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
                finanzas.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_extrabold));
                educacion.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
                ventas.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
                ingenieria.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
                recursosHumanos.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
                servicios.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
                asesoríaLegal.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
                artesYEntretenimiento.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
                otros.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
            }
        });

        educacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Animación del texto
                YoYo.with(Techniques.FlipInX)
                        .duration(450)
                        .repeat(0)
                        .playOn(educacion);

                salud.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
                tecnologia.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
                finanzas.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
                educacion.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_extrabold));
                ventas.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
                ingenieria.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
                recursosHumanos.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
                servicios.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
                asesoríaLegal.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
                artesYEntretenimiento.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
                otros.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
            }
        });

        ventas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Animación del texto
                YoYo.with(Techniques.FlipInX)
                        .duration(450)
                        .repeat(0)
                        .playOn(ventas);

                salud.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
                tecnologia.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
                finanzas.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
                educacion.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
                ventas.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_extrabold));
                ingenieria.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
                recursosHumanos.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
                servicios.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
                asesoríaLegal.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
                artesYEntretenimiento.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
                otros.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
            }
        });

        ingenieria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Animación del texto
                YoYo.with(Techniques.FlipInX)
                        .duration(450)
                        .repeat(0)
                        .playOn(ingenieria);

                salud.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
                tecnologia.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
                finanzas.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
                educacion.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
                ventas.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
                ingenieria.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_extrabold));
                recursosHumanos.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
                servicios.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
                asesoríaLegal.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
                artesYEntretenimiento.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
                otros.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
            }
        });

        recursosHumanos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Animación del texto
                YoYo.with(Techniques.FlipInX)
                        .duration(450)
                        .repeat(0)
                        .playOn(recursosHumanos);

                salud.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
                tecnologia.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
                finanzas.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
                educacion.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
                ventas.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
                ingenieria.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
                recursosHumanos.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_extrabold));
                servicios.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
                asesoríaLegal.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
                artesYEntretenimiento.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
                otros.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
            }
        });

        servicios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Animación del texto
                YoYo.with(Techniques.FlipInX)
                        .duration(450)
                        .repeat(0)
                        .playOn(servicios);

                salud.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
                tecnologia.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
                finanzas.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
                educacion.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
                ventas.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
                ingenieria.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
                recursosHumanos.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
                servicios.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_extrabold));
                asesoríaLegal.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
                artesYEntretenimiento.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
                otros.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
            }
        });

        asesoríaLegal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Animación del texto
                YoYo.with(Techniques.FlipInX)
                        .duration(450)
                        .repeat(0)
                        .playOn(asesoríaLegal);

                salud.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
                tecnologia.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
                finanzas.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
                educacion.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
                ventas.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
                ingenieria.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
                recursosHumanos.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
                servicios.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
                asesoríaLegal.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_extrabold));
                artesYEntretenimiento.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
                otros.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
            }
        });

        artesYEntretenimiento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Animación del texto
                YoYo.with(Techniques.FlipInX)
                        .duration(450)
                        .repeat(0)
                        .playOn(artesYEntretenimiento);

                salud.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
                tecnologia.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
                finanzas.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
                educacion.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
                ventas.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
                ingenieria.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
                recursosHumanos.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
                servicios.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
                asesoríaLegal.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
                artesYEntretenimiento.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_extrabold));
                otros.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
            }
        });

        otros.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Animación del texto
                YoYo.with(Techniques.FlipInX)
                        .duration(450)
                        .repeat(0)
                        .playOn(otros);

                salud.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
                tecnologia.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
                finanzas.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
                educacion.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
                ventas.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
                ingenieria.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
                recursosHumanos.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
                servicios.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
                asesoríaLegal.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
                artesYEntretenimiento.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold));
                otros.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_extrabold));
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
                /*Lo usé antes, ahora sirve para las pruebas
                //startActivity(new Intent(ContactActivity.this, DataActivity.class));
                //overridePendingTransition(R.anim.scale_in, R.anim.scale_out);
                 */
                openInfo(1);
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
                /*Lo usé antes, ahora sirve para las pruebas
                //startActivity(new Intent(ContactActivity.this, DataActivity.class));
                //overridePendingTransition(R.anim.scale_in, R.anim.scale_out);
                 */
                openInfo(2);
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
                /*Lo usé antes, ahora sirve para las pruebas
                //startActivity(new Intent(ContactActivity.this, DataActivity.class));
                //overridePendingTransition(R.anim.scale_in, R.anim.scale_out);
                 */
                openInfo(3);
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

        //Se accede a la información guardada en la base de datos
        DatabaseReference databaseReference;
        databaseReference = FirebaseDatabase.getInstance().getReference("1JcKn4lV9YC5cF8o_QyekJ7-72u-bRn748CLrLc9jTD0/workers");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                LinearLayout inflatedProposals = (LinearLayout) findViewById(R.id.contact_inflatedproposals_linearlayout);
                inflatedProposals.removeAllViews();

                //Para saber la cantidad de hijos (largo de la lista)
                int size = (int) snapshot.getChildrenCount();

                //Se crean los Strings que guardarán los datos
                String time = "";
                String job  = "";
                String name  = "";
                String description  = "";
                String phone = "";
                String student = "";

                for (int i=1; i<size+1; i++) {
                    time = snapshot.child(String.valueOf(i)).child("time").getValue(String.class);
                    job = snapshot.child(String.valueOf(i)).child("job").getValue(String.class);
                    name = snapshot.child(String.valueOf(i)).child("fullname").getValue(String.class);
                    //Todavía no hay una descripción de cada trabajador en la base de datos (arreglar en un futuro)
                    description = snapshot.child(String.valueOf(i)).child("mail").getValue(String.class);
                    phone = String.valueOf(snapshot.child(String.valueOf(i)).child("phone").getValue(Long.class));
                    student = snapshot.child(String.valueOf(i)).child("student").getValue(String.class);

                    createProposals(time, job, name, student);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "¡No se pudo acceder a las demás propuestas, revisá tu conexión!", Toast.LENGTH_LONG);
            }
        });
    }

    //Función para abrir el Dialog con más información de la persona seleccionada
    //En un futuro hay que añadir parámetros para que apenas abra tenga el nombre, mail y demás datos
    private void openInfo(int numberInfo) {
        infoDialog = new Dialog(ContactActivity.this);
        infoDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        infoDialog.getWindow().getAttributes().windowAnimations = R.style.InfoDialogAnimation;
        infoDialog.setCancelable(true);

        //Switch para saber qué información mostrar
        switch (numberInfo) { //No hay un caso default porque realmente no es necesario
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
        try{
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
        } catch (Exception e) {}

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

    private void createProposals(String time, String job, String name, String student) {
        //Se crea (infla) el layout con las propuestas
        LinearLayout inflatedProposals = (LinearLayout) findViewById(R.id.contact_inflatedproposals_linearlayout);
        View proposalToAdd = getLayoutInflater().inflate(R.layout.proposal_layout, inflatedProposals, false);
        inflatedProposals.addView(proposalToAdd);

        //Para convertir el formato de la variable "time" a texto
        time = time.substring(0, 10);
        time = time.replace("-", "");
        Log.d("OZ", time);
        String timeYear = time.substring(0, 4);
        String timeMonth = time.substring(4, 6);
        timeMonth = timeMonth.replaceFirst("^0+(?!$)", "");
        Log.d("OZ", timeMonth);
        String timeDay = time.substring(6, 8);
        timeDay = timeDay.replaceFirst("^0+(?!$)", "");
        Log.d("OZ", timeDay);

        //Para el mes se deben usar las siguientes líneas de código
        DateTimeFormatter fmt = new DateTimeFormatterBuilder().parseCaseInsensitive().appendPattern("MMMM").toFormatter(new Locale("es", "ES"));
        timeMonth = Month.of(Integer.parseInt(timeMonth)).getDisplayName(TextStyle.FULL_STANDALONE, new Locale("es", "ES"));
        timeMonth = timeMonth.substring(0,1).toUpperCase() + timeMonth.substring(1).toLowerCase();

        //Se les asigna el valor a los TextView
        TextView dateProposal = (TextView) proposalToAdd.findViewById(R.id.proposal_time_textview);
        dateProposal.setText(timeDay + " de " + timeMonth + " del " + timeYear);

        TextView jobProposal = (TextView) proposalToAdd.findViewById(R.id.proposal_titlerl_textview);
        jobProposal.setText(job);

        TextView nameProposal = (TextView) proposalToAdd.findViewById(R.id.proposal_subtitlerl_textview);
        nameProposal.setText(name);

        TextView descriptionProposal = (TextView) proposalToAdd.findViewById(R.id.proposal_descriptionrl_textview);
        final int randomYears = new Random().nextInt(15) + 2;
        descriptionProposal.setText(Html.fromHtml("Me desempeño como " + job.toLowerCase() + " hace más de " + randomYears + " años. Fui recomendado/a por " + student + ". " + "<font color='#3876F6'><u>Leer más.</u></font>"));
    }
}