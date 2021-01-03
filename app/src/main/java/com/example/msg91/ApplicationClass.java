package com.example.msg91;

import android.app.Application;

import com.msg91.sendotpandroid.library.internal.SendOTP;

public class ApplicationClass extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SendOTP.initializeApp(this,"343141A0eUofjHNg5f73eff8P1");        //initialization
    }
}
