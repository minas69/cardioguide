package com.example.medicalapp

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.lang.Exception


class InputSerializer : KSerializer<Input> {

    companion object {
        const val TEXT_INPUT_TYPE = "text"
        const val DATE_INPUT_TYPE = "date"
        const val OPTIONS_INPUT_TYPE = "options"
        const val INTEGER_INPUT_TYPE = "unsigned"
        const val CHECKBOX_INPUT_TYPE = "checkbox"
        const val RADIO_GROUP_INPUT_TYPE = "radioGroup"
        const val CHECKBOX_GROUP_INPUT_TYPE = "checkboxGroup"
    }

    override val descriptor: SerialDescriptor = InputSurrogate.serializer().descriptor

    override fun deserialize(decoder: Decoder): Input {
        val surrogate = decoder.decodeSerializableValue(InputSurrogate.serializer())

        return with(surrogate) {
            when (inputType) {
                TEXT_INPUT_TYPE -> TextInput(id, name, required)
                DATE_INPUT_TYPE -> DateInput(id, name, required)
                OPTIONS_INPUT_TYPE -> OptionsInput(id, name, required, options!!)
                INTEGER_INPUT_TYPE -> IntegerInput(id, name, required, suffixText)
                CHECKBOX_INPUT_TYPE -> CheckBox(id, name, required, thenAttributes)
                RADIO_GROUP_INPUT_TYPE -> RadioGroup(id, name, required, options!!, thenAttributes)
                CHECKBOX_GROUP_INPUT_TYPE -> CheckboxGroup(id, name, required, options!!)
                else -> throw Exception("There's no such inputType=$inputType")
            }
        }
    }

    override fun serialize(encoder: Encoder, value: Input) {
        TODO("Not yet implemented")
    }
}

//@Serializable(with = StepSerializer::class)
@Serializable
data class Step(val id: Int, val name: String, val attributes: List<Input>)

@Serializable(with = InputSerializer::class)
sealed class Input(val id: Int, val name: String, val required: Boolean) {

    abstract fun isFilled(): Boolean

    abstract fun toHashMap(): HashMap<String, *>

}

class TextInput(
    id: Int,
    name: String,
    required: Boolean,
    var input: String = ""
) : Input(id, name, required) {

    override fun isFilled() = input.isNotEmpty()

    override fun toHashMap() = hashMapOf(
        "id" to id,
        "value" to input
    )

}

class DateInput(
    id: Int,
    name: String,
    required: Boolean,
    var input: String = ""
) : Input(id, name, required) {

    override fun isFilled() = input.isNotEmpty()

    override fun toHashMap() = hashMapOf(
        "id" to id,
        "value" to input
    )

}

class OptionsInput(
    id: Int,
    name: String,
    required: Boolean,
    val options: List<String>
) : Input(id, name, required) {

    var selected: Int? = null

    override fun isFilled() = selected != null

    override fun toHashMap() = hashMapOf(
        "id" to id,
        "value" to selected
    )

}

class IntegerInput(
    id: Int,
    name: String,
    required: Boolean,
    val suffix: String?,
    var input: Int? = null
) : Input(id, name, required) {

    override fun isFilled() = input != null

    override fun toHashMap() = hashMapOf(
        "id" to id,
        "value" to input
    )

}

class CheckBox(
    id: Int,
    name: String,
    required: Boolean,
    val thenAttributes: List<Input>? = null
) : Input(id, name, required) {

    var isChecked: Boolean = false

    override fun isFilled() = true

    override fun toHashMap() = hashMapOf(
        "id" to id,
        "value" to isChecked
    )

}

class RadioGroup(
    id: Int,
    name: String,
    required: Boolean,
    val options: List<String>,
    val thenAttributes: List<Input>? = null
) : Input(id, name, required) {

    var checked: Int? = null

    override fun isFilled() = checked != null

    override fun toHashMap() = hashMapOf(
        "id" to id,
        "value" to checked
    )

}

class CheckboxGroup(
    id: Int,
    name: String,
    required: Boolean,
    val options: List<String>
) : Input(id, name, required) {

    var checked = mutableListOf<Int>()

    override fun isFilled() = true

    override fun toHashMap() = hashMapOf(
        "id" to id,
        "value" to checked
    )

}
