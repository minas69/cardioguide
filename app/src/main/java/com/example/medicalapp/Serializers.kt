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
        const val PHOTOS_INPUT_TYPE = "photo"
    }

    override val descriptor: SerialDescriptor = InputSurrogate.serializer().descriptor

    override fun deserialize(decoder: Decoder): Input {
        val surrogate = decoder.decodeSerializableValue(InputSurrogate.serializer())

        return with(surrogate) {
            when (inputType) {
                TEXT_INPUT_TYPE -> TextInput(id, name, required, helperText)
                DATE_INPUT_TYPE -> DateInput(id, name, min, max, required)
                OPTIONS_INPUT_TYPE -> OptionsInput(id, name, required, options!!)
                INTEGER_INPUT_TYPE -> IntegerInput(id, name, required, suffixText, helperText)
                FLOAT_INPUT_TYPE -> FloatInput(id, name, required, suffixText, helperText)
                CHECKBOX_INPUT_TYPE -> CheckBoxInput(id, name, required, thenAttributes)
                RADIO_GROUP_INPUT_TYPE -> RadioGroupInput(id, name, required, options!!, thenAttributes)
                CHECKBOX_GROUP_INPUT_TYPE -> CheckboxGroupInput(id, name, required, options!!)
                PHOTOS_INPUT_TYPE -> PhotosInput(id, name, max!!.toInt(), required)
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
data class Step(
    val id: Int,
    val name: String,
    val containsRequired: Boolean = false,
    val attributes: List<Input>
): Parcelable

@Serializable(with = InputSerializer::class)
sealed class Input: Parcelable {

    abstract fun isRequired(): Boolean

    abstract fun getIdd(): String

}

@Parcelize
class TextInput(
    val id: String,
    val name: String,
    val required: Boolean,
    val helperText: String?,
    var input: CharSequence = ""
) : Input() {

    override fun isRequired() = required

    override fun getIdd() = id

}

@Parcelize
class DateInput(
    val id: String,
    val name: String,
    val min: Long?,
    val max: Long?,
    val required: Boolean,
    var input: String = ""
) : Input() {

    override fun isRequired() = required

    override fun getIdd() = id

}

@Parcelize
class OptionsInput(
    val id: String,
    val name: String,
    val required: Boolean,
    val options: List<String>
) : Input() {

    var selected: Int? = null

    override fun isRequired() = required

    override fun getIdd() = id

}

@Parcelize
class IntegerInput(
    val id: String,
    val name: String,
    val required: Boolean,
    val suffix: String?,
    val helperText: String?,
    var input: Int? = null
) : Input() {

    override fun isRequired() = required

    override fun getIdd() = id

}

@Parcelize
class FloatInput(
    val id: String,
    val name: String,
    val required: Boolean,
    val suffix: String?,
    val helperText: String?,
    var input: Float? = null
) : Input() {

    override fun isRequired() = required

    override fun getIdd() = id

}

@Parcelize
class CheckBoxInput(
    val id: String,
    val name: String,
    val required: Boolean,
    val thenAttributes: List<Input>? = null
) : Input() {

    var isChecked: Boolean = false

    override fun isRequired() = required

    override fun getIdd() = id

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

    override fun isRequired() = required

    override fun getIdd() = id

}

@Parcelize
class CheckboxGroupInput(
    val id: String,
    val name: String,
    val required: Boolean,
    val options: List<String>
) : Input() {

    var checked = mutableListOf<Int>()

    override fun isRequired() = required

    override fun getIdd() = id

}

@Parcelize
class PhotosInput(
    val id: String,
    val name: String,
    val max: Int,
    val required: Boolean,
) : Input() {

    var value = 0

    override fun isRequired() = required

    override fun getIdd() = id

}
