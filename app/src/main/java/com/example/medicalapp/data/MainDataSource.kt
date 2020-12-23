package com.example.medicalapp.data

import com.example.medicalapp.Input
import com.google.firebase.functions.FirebaseFunctions

class MainDataSource {

    private val functions: FirebaseFunctions by lazy { FirebaseFunctions.getInstance() }

//    suspend fun sendData(data: Data): Report {
//        val dataHashMap = hashMapOf(
//            "surname" to data.surname,
//            "age" to data.age,
//            "weight" to data.weight,
//            "gender" to data.gender,
//            "pressure" to data.pressure,
//            "cholesterol" to data.cholesterol,
//            "ldl" to data.ldl,
//            "smoking" to data.smoking,
//            "lowRiskCountry" to data.lowRiskCountry
//        )
//
//        @Suppress("UNCHECKED_CAST")
//        val result = functions
//            .getHttpsCallable("api/report")
//            .call(dataHashMap)
//            .await()
//            .data as Map<String, Any>
//
//        val jsonString = JSONObject(result).toString()
//        return Json.decodeFromString(jsonString)
//    }

    suspend fun post(data: List<Input>): String {
        val payload = mutableListOf<HashMap<String, *>>()
        data.forEach {
            payload.add(it.toHashMap())
        }

        @Suppress("UNCHECKED_CAST")
        val result = functions
            .getHttpsCallable("api/post")
            .call(payload)
            .await()
            .data as String

        return result

//        val jsonString = JSONObject(result).toString()
//        return Json.decodeFromString(jsonString)
    }
}