package com.example.medicalapp.data

import com.example.medicalapp.Input
import com.example.medicalapp.Step


object MainRepository {

    private val dataSource = MainDataSource()

    suspend fun post(steps: List<Step>): String {
        val data = mutableListOf<Input>()
        steps.forEach { data.addAll(it.attributes) }
        return dataSource.post(data)
    }
}