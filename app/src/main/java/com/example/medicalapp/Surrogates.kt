package com.example.medicalapp

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class BlockSurrogate(val id: Int, val name: String, val attributes: List<Input>)

@Serializable
class InputSurrogate(
    val id: Int,
    val name: String,
    val inputType: String,
    val required: Boolean = false,
    val options: List<String>? = null,
    val suffixText: String? = "",
    val thenAttributes: List<Input>? = null
)