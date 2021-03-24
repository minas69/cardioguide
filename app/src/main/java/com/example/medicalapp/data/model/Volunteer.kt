package com.example.medicalapp.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
@Parcelize
data class Volunteer(
    val uid: String,
    val email: String?
) : Parcelable