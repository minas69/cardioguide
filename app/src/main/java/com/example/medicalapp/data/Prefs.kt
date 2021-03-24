package com.example.medicalapp.data

import android.content.Context
import android.content.SharedPreferences

class Prefs(context: Context) {

    private val preferences: SharedPreferences
            = context.getSharedPreferences(APP_PREF_NAME, Context.MODE_PRIVATE)

    var firstName: String
        get() = preferences.getString(FIRST_NAME_KEY, "")!!
        set(value) = preferences.edit().putString(FIRST_NAME_KEY, value).apply()

    var lastName: String
        get() = preferences.getString(LAST_NAME_KEY, "")!!
        set(value) = preferences.edit().putString(LAST_NAME_KEY, value).apply()

    var patronymic: String
        get() = preferences.getString(PATRONYMIC_KEY, "")!!
        set(value) = preferences.edit().putString(PATRONYMIC_KEY, value).apply()

    companion object {

        private const val APP_PREF_NAME = "CardioguidePref"

        private const val FIRST_NAME_KEY = "firstName"
        private const val LAST_NAME_KEY = "lastName"
        private const val PATRONYMIC_KEY = "patronymic"

    }
}