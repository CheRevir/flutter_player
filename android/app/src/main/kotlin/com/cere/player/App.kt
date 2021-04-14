package com.cere.player

import android.app.Application
import android.util.Log

/**
 * Created by CheRevir on 2021/4/14
 */
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Log.e("TAG", "App -> onCreate: ")
    }
}