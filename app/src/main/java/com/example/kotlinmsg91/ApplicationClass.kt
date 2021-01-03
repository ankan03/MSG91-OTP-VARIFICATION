package com.example.kotlinmsg91

import android.app.Application
import com.msg91.sendotpandroid.library.internal.SendOTP

class ApplicationClass : Application() {
    override fun onCreate() {
        super.onCreate()
        SendOTP.initializeApp(this, "343141A0eUofjHNg5f73eff8P1")
    }
}