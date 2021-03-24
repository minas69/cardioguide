package com.example.medicalapp.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class ResultResponse(
    val id: String,
    val createdAt: Date,
    val createdBy: Volunteer,
    val SCORE: Double,
    val CKD_EPI: Double,
    val BMI: Double
) : Parcelable