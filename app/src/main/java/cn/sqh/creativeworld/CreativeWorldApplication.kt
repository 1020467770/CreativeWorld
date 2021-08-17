package cn.sqh.creativeworld

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

class CreativeWorldApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }
}