package com.example.medicalapp.data

import com.example.medicalapp.data.model.Volunteer
import com.google.firebase.auth.FirebaseUser
import java.lang.Exception

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

object LoginRepository {

    private val dataSource = LoginDataSource()

    // in-memory cache of the loggedInUser object
    var user: FirebaseUser? = null
        private set

    val isLoggedIn: Boolean
        get() = user != null

    init {
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
        user = null
    }

    fun logout() {
        user = null
        dataSource.logout()
    }

    suspend fun login(username: String, password: String): Volunteer {
        val user = dataSource.login(username, password)

        user?.let {
            setLoggedInUser(it)
            return Volunteer(user.uid, user.displayName)
        }

        throw Exception("lox pidor")
    }

    private fun setLoggedInUser(loggedInUser: FirebaseUser) {
        this.user = loggedInUser
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }
}