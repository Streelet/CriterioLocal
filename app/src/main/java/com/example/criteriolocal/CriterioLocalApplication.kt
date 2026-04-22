package com.example.criteriolocal

import android.app.Application
import com.example.criteriolocal.core.di.AppContainer
import com.example.criteriolocal.core.di.DefaultAppContainer

class CriterioLocalApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}
