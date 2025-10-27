package com.dreamyducks.navcook

import android.app.Application
import com.dreamyducks.navcook.data.AppContainer
import com.dreamyducks.navcook.data.DefaultAppContainer
class NavCookApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}