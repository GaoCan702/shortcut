package com.example.shortcutpractice

import android.app.Application

class ShortcutPracticeApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        //log("ShortcutPracticeApplication onCreate")
        println("user perference init start")
        UserPreference.init(this)
        println("user perference init end")

    }
}