package com.example.medicalapp.data

import com.example.medicalapp.data.model.Data

object MainRepository {

    private val dataSource = MainDataSource()

    suspend fun sendData(data: Data) = dataSource.sendData(data)
}