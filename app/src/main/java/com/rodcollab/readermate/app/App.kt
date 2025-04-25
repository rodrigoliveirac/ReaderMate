package com.rodcollab.readermate.app

import android.app.Application
import com.rodcollab.readermate.app.di.ktorModule
import com.rodcollab.readermate.app.di.roomModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(roomModule, ktorModule)
        }
    }
}