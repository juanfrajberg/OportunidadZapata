package com.juanfrajberg.oportunidadzapata;

import android.app.Application;
import android.content.Intent;

public class OportunidadZapata extends Application {
    //Variable que almacena el número de propuestas que hay (terminar de configurar bien)
    public int proposalNumbers;

    //Para llamar a este valor desde cualquier actividad
    //int proposalNumbers = ((OportunidadZapata) getApplicationContext()).proposalNumbers;

    @Override
    public void onCreate() {
        super.onCreate();

        //Código que envía una notificación cuando se añade una propuesta
        Intent intentToOpenActivity = new Intent(getApplicationContext(), ContactActivity.class);
        NotificationBroadcast.showNotification(getApplicationContext(), "Oportunidad Zapata", getApplicationContext().getResources().getString(R.string.newproposalnotificacion_text), intentToOpenActivity, 1);
    }
}
