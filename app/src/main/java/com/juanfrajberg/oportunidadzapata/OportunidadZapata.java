package com.juanfrajberg.oportunidadzapata;

import android.app.Application;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class OportunidadZapata extends Application {
    //Variable que almacena el número de propuestas que hay (terminar de configurar bien)
    public int proposalNumbers;

    //Para llamar a este valor desde cualquier actividad
    //int proposalNumbers = ((OportunidadZapata) getApplicationContext()).proposalNumbers;

    @Override
    public void onCreate() {
        //Código básico para que se inicie la clase
        super.onCreate();

        //Se lee el número de las propuestas actuales y al cambiar se envía una notificación
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("1JcKn4lV9YC5cF8o_QyekJ7-72u-bRn748CLrLc9jTD0/application/1");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Por si se borran los datos o no se puede acceder a ellos
                try {
                    int numberProposals = snapshot.child("numberProposals").getValue(Integer.class);
                    sendNewProposalNotification((numberProposals));
                } catch (Exception e) {}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //Función para enviar una notificación sobre propuestas nuevas
    public void sendNewProposalNotification(long numberProposals) {
        //Código que envía una notificación cuando se añade una propuesta
        Intent intentToOpenActivity = new Intent(getApplicationContext(), ContactActivity.class);
        NotificationBroadcast.showNotification(getApplicationContext(), "Oportunidad Zapata", getApplicationContext().getResources().getString(R.string.newproposalnotificacion_text), intentToOpenActivity, 1);
    }
}
