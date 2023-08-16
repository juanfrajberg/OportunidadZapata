package com.juanfrajberg.oportunidadzapata;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.TextView;

//Clase para el efecto de máquina de escribir en el chat con AI usado en ConsultasActivity
public class Typewriter extends TextView {

    private CharSequence mText;
    private int mIndex;
    private long mDelay = 150; // in ms
    private static boolean hasFinished = true;

    public Typewriter(Context context) {
        super(context);
    }

    public Typewriter(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private Handler mHandler = new Handler();

    private Runnable characterAdder = new Runnable() {

        @Override
        public void run() {
            setText(mText.subSequence(0, mIndex++));

            if (mIndex <= mText.length()) {
                mHandler.postDelayed(characterAdder, mDelay);
                hasFinished = false;
            }
            else {
                hasFinished = true;
                ConsultasActivity.scrollToBottom();
            }
        }
    };

    public void animateText(CharSequence txt) {
        mText = txt;
        mIndex = 0;

        setText("");
        mHandler.removeCallbacks(characterAdder);
        mHandler.postDelayed(characterAdder, mDelay);
    }

    public void setCharacterDelay(long m) {
        mDelay = m;
    }

    public static boolean hasFinished() {
        return hasFinished;
    }
}
