package com.example.medicalapp.data

import com.example.medicalapp.data.model.Data
import com.example.medicalapp.data.model.Report
import com.google.firebase.functions.FirebaseFunctions
import kotlinx.android.synthetic.main.content_form.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import org.json.JSONObject

class MainDataSource {

    private val functions: FirebaseFunctions by lazy { FirebaseFunctions.getInstance() }

    suspend fun sendData(data: Data): Report {
        val dataHashMap = hashMapOf(
            "surname" to data.surname,
            "age" to data.age,
            "weight" to data.weight,
            "gender" to data.gender,
            "pressure" to data.pressure,
            "cholesterol" to data.cholesterol,
            "ldl" to data.ldl,
            "smoking" to data.smoking,
            "lowRiskCountry" to data.lowRiskCountry
        )

        @Suppress("UNCHECKED_CAST")
        val result = functions
            .getHttpsCallable("api/report")
            .call(dataHashMap)
            .await()
            .data as Map<String, Any>

        val jsonString = JSONObject(result).toString()
        return Json.decodeFromString(jsonString)
    }
}