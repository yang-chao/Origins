package com.assassin.origins.ui.base

import android.app.Application

class BaseApplication : Application() {

    companion object {
        lateinit var instance: Application
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

}