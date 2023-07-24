package com.juanfrajberg.oportunidadzapata;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

public class NotificationBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int reqCode = 1;
        //Este intent lo que hace es abrir la actividad a la que se nos llevará al hacer clic en la notificación
        Intent intent1 = new Intent(context, StartActivity.class);
        try {
            NotificationBroadcast.showNotification(context, "Oportunidad Zapata", context.getResources().getString(R.string.notificacion_text), intent1, reqCode);
        } catch (Exception e) { //Por si algo funciona mal en versionas distintas de Android, por el momento en mis dispositivos todo perfecto
            Toast.makeText(context, "No se pudo enviar la notificación.", Toast.LENGTH_SHORT).show();
        }
    }

    //Función para poder enviar una notificación
    /**
     * @param context --> Contexto
     * @param title   --> Título a mostrar
     * @param message --> Mensaje (detalle) a mostrar
     * @param intent  --> Lo que debería pasar al hacer clic en la notificación
     * @param reqCode --> Código único para la notificación
     */

    @TargetApi(Build.VERSION_CODES.O)
    public static void showNotification(Context context, String title, String message, Intent intent, int reqCode) {
        PendingIntent pendingIntent = PendingIntent.getActivity(context, reqCode, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT); //PendingIntent.FLAG_ONE_SHOT |
        String CHANNEL_ID = "oportunidad_zapata"; //El ID del canal

        //Lo que está comentado que no funciona en el J5 Prime, sí funciona en el emulador :)
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setStyle(new NotificationCompat.BigTextStyle())
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                //.setOngoing(true) //Si no quiero que la notificación se pueda borrar
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(pendingIntent);

        //Si la versión de Android es la 8, como en el J5 Prime, entonces se ejecuta la parte de notificación que funciona para ese dispositivo
        if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.O) { //Android 8 (Oreo, como el J5 Prime)
            notificationBuilder.setSmallIcon(R.mipmap.ic_launcher_round);
        } else { //Demás versiones
            notificationBuilder.setSmallIcon(R.drawable.notification_app_icon);
            notificationBuilder.setColorized(true);
            notificationBuilder.setColor(Color.parseColor("#3876F6"));
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Oportunidad Zapata";// The user-visible name of the channel.
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            notificationManager.createNotificationChannel(mChannel);
        }
        notificationManager.notify(reqCode, notificationBuilder.build()); // 0 es el código de requisito (request code), debe ser un ID único
    }
}
