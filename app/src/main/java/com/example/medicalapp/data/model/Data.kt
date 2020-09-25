package com.example.medicalapp.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class Data(
    val surname: String,
    val age: Int,
    val weight: Int,
    val gender: String,
    val pressure: Int,
    val cholesterol: Int,
    val ldl: Int,
    val smoking: Boolean,
    val lowRiskCountry: Boolean
) : Parcelable