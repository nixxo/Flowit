package com.bytehamster.flowit.state;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.bytehamster.flowit.GLRenderer;
import com.bytehamster.flowit.animation.Animation;
import com.bytehamster.flowit.animation.TranslateAnimation;
import com.bytehamster.flowit.objects.Plane;
import com.bytehamster.flowit.objects.TextureCoordinates;

public class ExitState extends State {
    @SuppressLint("StaticFieldLeak")
    private static ExitState instance;

    private ExitState() {

    }

    public static ExitState getInstance() {
        if (instance == null) {
            instance = new ExitState();
        }
        return instance;
    }

    @Override
    protected void initialize(GLRenderer glRenderer) {

    }

    @Override
    public void entry() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getActivity().finish();
            }
        }, Animation.DURATION_LONG);
    }

    @Override
    public void exit() {

    }

    @Override
    public State next() {
        return this;
    }

    @Override
    public void onKeyDown(int keyCode, KeyEvent event) {

    }

    @Override
    public void onTouchEvent(MotionEvent event) {

    }
}
