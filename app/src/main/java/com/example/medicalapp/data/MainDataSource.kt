package com.example.medicalapp.data

import android.util.Log
import com.example.medicalapp.Input
import com.example.medicalapp.data.model.ResultResponse
import com.google.firebase.functions.FirebaseFunctions

class MainDataSource {

    private val functions: FirebaseFunctions by lazy { FirebaseFunctions.getInstance() }

    suspend fun post(payload: List<Map<String, *>>): ResultResponse {

        @Suppress("UNCHECKED_CAST")
        val result = functions
            .getHttpsCallable("api/post")
            .call(payload)
            .await()
            .data as HashMap<String, *>

        return ResultResponse(result["hdr"] as Double, result["gfr"] as Double, result["bmi"] as Double)

//        val jsonString = JSONObject(result).toString()
//        return Json.decodeFromString(jsonString)
    }
}