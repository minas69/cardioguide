package com.example.medicalapp.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ResultResponse(
    val id: String,
    val SCORE: Double,
    val CKD_EPI: Double,
    val BMI: Double
) : Parcelable