package com.juanfrajberg.oportunidadzapata;

import android.app.Application;
import android.os.Handler;

public class WiFiInformation extends Application {

    //Devuelve el valor de activityVisible
    public static boolean isActivityVisible() {
        return activityVisible;
    }

    //Devuelve el valor de showAgain
    public static boolean isShowAgain() {
        return showAgain;
    }

    //Se llama cuando la actividad (Dialog) entra en onResume()
    public static void activityResumed() {
        Handler waitUntilActivityOpened = new Handler();
        waitUntilActivityOpened.postDelayed(new Runnable() {
            public void run() {
                try {StartActivity.setElementsLayoutClickeable(false);} catch (Exception e) {}
                try {HomeActivity.setElementsLayoutClickeable(false);} catch (Exception e) {}
                try {BlogActivity.setElementsLayoutClickeable(false);} catch (Exception e) {}
                try {ConsultasActivity.setElementsLayoutClickeable(false);} catch (Exception e) {}
                try {ContactActivity.setElementsLayoutClickeable(false);} catch (Exception e) {}
                try {ProposalActivity.setElementsLayoutClickeable(false);} catch (Exception e) {}
                try {PostActivity.setElementsLayoutClickeable(false);} catch (Exception e) {}
                activityVisible = true;
            }
        }, 450); //Lo que dura la animación de la actividad (Dialog) para abrirse
    }

    public static void activityPaused() {
        Handler waitUntilActivityClosed = new Handler();
        waitUntilActivityClosed.postDelayed(new Runnable() {
            public void run() {
                try {StartActivity.setElementsLayoutClickeable(true);} catch (Exception e) {}
                try {HomeActivity.setElementsLayoutClickeable(true);} catch (Exception e) {}
                try {BlogActivity.setElementsLayoutClickeable(true);} catch (Exception e) {}
                try {ConsultasActivity.setElementsLayoutClickeable(true);} catch (Exception e) {}
                try {ContactActivity.setElementsLayoutClickeable(true);} catch (Exception e) {}
                try {ProposalActivity.setElementsLayoutClickeable(true);} catch (Exception e) {}
                try {PostActivity.setElementsLayoutClickeable(true);} catch (Exception e) {}
                activityVisible = false;
            }
        }, 450); //Lo que dura la animación de la actividad (Dialog) para cerrarse
    }

    private static boolean activityVisible;
    public static boolean showAgain = true;
    public static String lastState = "ON"; //String para guardar el último estado del Wi-Fi
}