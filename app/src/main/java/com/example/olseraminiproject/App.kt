package com.example.olseraminiproject

import android.app.Application
import com.example.olseraminiproject.di.dataSource
import org.koin.android.ext.android.startKoin

class App: Application() {
    override fun onCreate() {
        super.onCreate()

        // start Koin context
        startKoin(this@App, dataSource)
    }
}