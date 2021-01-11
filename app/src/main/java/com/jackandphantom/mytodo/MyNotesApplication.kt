package com.jackandphantom.mytodo

import android.app.Application
import com.jackandphantom.mytodo.di.AppComponent
import com.jackandphantom.mytodo.di.DaggerAppComponent

class MyNotesApplication : Application() {

    val appComponent : AppComponent by lazy {
        initializeComponent()
    }

    open fun initializeComponent () : AppComponent {
        return DaggerAppComponent.factory().create(applicationContext)
    }

}