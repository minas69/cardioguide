package com.example.medicalapp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Report(
    val risk: String,
    val wideRisk: String,
    val optimalRisk: String,
    val statin: String,
    val recommendation: String
) {
    init {
        require(risk.isNotEmpty()) { "risk cannot be empty" }
        require(wideRisk.isNotEmpty()) { "wideRisk cannot be empty" }
        require(optimalRisk.isNotEmpty()) { "optimalRisk cannot be empty" }
        require(statin.isNotEmpty()) { "statin cannot be empty" }
        require(recommendation.isNotEmpty()) { "recommendation cannot be empty" }
    }
}