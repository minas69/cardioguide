package com.example.medicalapp

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.lang.Exception

class BlocksSerializer : KSerializer<Block> {
    override val descriptor: SerialDescriptor = BlockSurrogate.serializer().descriptor

    override fun deserialize(decoder: Decoder): Block {
        val surrogate = decoder.decodeSerializableValue(BlockSurrogate.serializer())

        val attributes = mutableListOf<Input>()
        for (attributeSurrogate in surrogate.attributes) {
            with (attributeSurrogate) {
                val input = when (inputType) {
                    "text" -> TextInput(name, required)
                    "date" -> DateInput(name, required)
                    "options" -> OptionsInput(name, required, options!!)
                    "unsigned" -> IntegerInput(name, required, suffixText)
                    else -> throw Exception("There's no such inputType=$inputType")
                }
                attributes.add(input)
            }
        }

        return Block(surrogate.name, attributes)
    }

    override fun serialize(encoder: Encoder, value: Block) {
        TODO("Not yet implemented")
    }
}

@Serializable(with = BlocksSerializer::class)
data class Block(val name: String, val attributes: List<Input>)

sealed class Input(val name: String, val required: Boolean)

class TextInput(name: String, required: Boolean) : Input(name, required)
class DateInput(name: String, required: Boolean) : Input(name, required)
class OptionsInput(name: String, required: Boolean, val options: List<String>) : Input(name, required)
class IntegerInput(name: String, required: Boolean, val suffix: String?) : Input(name, required)
