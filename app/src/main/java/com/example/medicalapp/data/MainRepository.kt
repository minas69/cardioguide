package com.example.medicalapp.data

import com.example.medicalapp.data.model.ResultResponse


object MainRepository {

    private val dataSource = MainDataSource()

    suspend fun post(inputs: Map<String, *>): ResultResponse {
        val data = mutableListOf<HashMap<String, *>>()
        inputs.forEach { input ->
            data.add(hashMapOf("id" to input.key, "value" to input.value))
        }
        return dataSource.post(data)
    }
}
