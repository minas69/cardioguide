package com.example.medicalapp

import androidx.multidex.MultiDexApplication
import android.content.Context
import com.example.medicalapp.data.Prefs
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase

class App : MultiDexApplication() {

    private val functions: FirebaseFunctions by lazy { Firebase.functions }

    lateinit var prefs: Prefs

    override fun onCreate() {
        super.onCreate()
//        functions.useEmulator("192.168.1.40", 5000)

        prefs = Prefs(applicationContext)
    }

}