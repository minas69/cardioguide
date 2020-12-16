package com.example.medicalapp

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("Block")
class BlockSurrogate(val name: String, val attributes: List<AttributeSurrogate>) {

}

@Serializable
@SerialName("Attribute")
class AttributeSurrogate(
    val name: String,
    val inputType: String,
    val required: Boolean = false,
    val options: List<String>? = null,
    val suffixText: String? = ""
) {

}
