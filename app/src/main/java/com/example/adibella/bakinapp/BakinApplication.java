package com.example.adibella.bakinapp;

import android.app.Application;

import timber.log.Timber;

public class BakinApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Timber.plant(new Timber.DebugTree() {
            @Override
            protected String createStackElementTag(StackTraceElement element) {
                return String.format("[%1$s][%4$s.%2$s(%3$s)]",
                        "BAKINAPP DEBUG",
                        element.getMethodName(),
                        element.getLineNumber(),
                        super.createStackElementTag(element));
            }
        });
    }
}
