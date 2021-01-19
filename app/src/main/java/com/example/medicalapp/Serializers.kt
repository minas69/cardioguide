package com.example.medicalapp

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
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
        const val FLOAT_INPUT_TYPE = "float"
        const val CHECKBOX_INPUT_TYPE = "checkbox"
        const val RADIO_GROUP_INPUT_TYPE = "radioGroup"
        const val CHECKBOX_GROUP_INPUT_TYPE = "checkboxGroup"
        const val SLIDER_INPUT_TYPE = "slider"
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
                FLOAT_INPUT_TYPE -> FloatInput(id, name, required, suffixText)
                CHECKBOX_INPUT_TYPE -> CheckBoxInput(id, name, required, thenAttributes)
                RADIO_GROUP_INPUT_TYPE -> RadioGroupInput(id, name, required, options!!, thenAttributes)
                CHECKBOX_GROUP_INPUT_TYPE -> CheckboxGroupInput(id, name, required, options!!)
//                SLIDER_INPUT_TYPE -> SliderInput(id, name, required, Pair(from!!, to!!))
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
@Parcelize
data class Step(val id: Int, val name: String, val attributes: List<Input>): Parcelable

@Serializable(with = InputSerializer::class)
sealed class Input: Parcelable {

    abstract fun isFilled(): Boolean

    abstract fun toHashMap(): HashMap<String, *>

}

@Parcelize
class TextInput(
    val id: String,
    val name: String,
    val required: Boolean,
    var input: CharSequence = ""
) : Input() {

    override fun isFilled() = input.isNotEmpty()

    override fun toHashMap() = hashMapOf(
        "id" to id,
        "value" to input
    )

}

@Parcelize
class DateInput(
    val id: String,
    val name: String,
    val required: Boolean,
    var input: String = ""
) : Input() {

    override fun isFilled() = input.isNotEmpty()

    override fun toHashMap() = hashMapOf(
        "id" to id,
        "value" to input
    )

}

@Parcelize
class OptionsInput(
    val id: String,
    val name: String,
    val required: Boolean,
    val options: List<String>
) : Input() {

    var selected: Int? = null

    override fun isFilled() = selected != null

    override fun toHashMap() = hashMapOf(
        "id" to id,
        "value" to selected
    )

}

@Parcelize
class IntegerInput(
    val id: String,
    val name: String,
    val required: Boolean,
    val suffix: String?,
    var input: Int? = null
) : Input() {

    override fun isFilled() = input != null

    override fun toHashMap() = hashMapOf(
        "id" to id,
        "value" to input
    )

}

@Parcelize
class FloatInput(
    val id: String,
    val name: String,
    val required: Boolean,
    val suffix: String?,
    var input: Float? = null
) : Input() {

    override fun isFilled() = input != null

    override fun toHashMap() = hashMapOf(
        "id" to id,
        "value" to input
    )

}

@Parcelize
class CheckBoxInput(
    val id: String,
    val name: String,
    val required: Boolean,
    val thenAttributes: List<Input>? = null
) : Input() {

    var isChecked: Boolean = false

    override fun isFilled() = true

    override fun toHashMap() = hashMapOf(
        "id" to id,
        "value" to isChecked
    )

}

@Parcelize
class RadioGroupInput(
    val id: String,
    val name: String,
    val required: Boolean,
    val options: List<String>,
    val thenAttributes: List<Input>? = null
) : Input() {

    var checked: Int? = null

    override fun isFilled() = checked != null

    override fun toHashMap() = hashMapOf(
        "id" to id,
        "value" to checked
    )

}

@Parcelize
class CheckboxGroupInput(
    val id: String,
    val name: String,
    val required: Boolean,
    val options: List<String>
) : Input() {

    var checked = mutableListOf<Int>()

    override fun isFilled() = true

    override fun toHashMap() = hashMapOf(
        "id" to id,
        "value" to checked
    )

}

//class SliderInput(
//    id: Int,
//    name: String,
//    required: Boolean,
//    val range: Pair<Int, Int>
//) : Input(id, name, required) {
//
//    var value = 0
//
//    override fun isFilled() = value != 0
//
//    override fun toHashMap() = hashMapOf(
//        "id" to id,
//        "value" to value
//    )
//
//}
