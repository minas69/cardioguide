package com.example.medicalapp.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ResultResponse(val hdr: Double, val gfr: Double, val bmi: Double) : Parcelable