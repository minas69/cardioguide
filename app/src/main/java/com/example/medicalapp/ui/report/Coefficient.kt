package com.example.medicalapp.ui.report

import kotlinx.serialization.Serializable

@Serializable
data class Coefficient(val name: String, val options: List<CoefficientOption>)

@Serializable
data class CoefficientOption(val description: String, val comments: String)