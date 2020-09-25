package com.example.medicalapp.data

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource {

    private val auth: FirebaseAuth by lazy { Firebase.auth }

    suspend fun login(email: String, password: String): FirebaseUser?
            = auth.signInWithEmailAndPassword(email, password).await().user

    fun logout() {
        auth.signOut()
    }

    private suspend fun <T> Task<T>.await(): T {
        if (isComplete) {
            val e = exception
            return if (e == null) {
                if (isCanceled) {
                    throw CancellationException(
                        "Task $this was cancelled normally.")
                } else {
                    result!!
                }
            } else {
                throw e
            }
        }

        return suspendCancellableCoroutine { cont ->
            addOnCompleteListener {
                val e = exception
                if (e == null) {
                    if (isCanceled) cont.cancel() else cont.resume(result!!)
                } else {
                    cont.resumeWithException(e)
                }
            }
        }
    }
}