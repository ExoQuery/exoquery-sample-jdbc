package io.exoquery.example

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

class SerializerExtender<T, R>(private val original: KSerializer<T>) {
  private lateinit var mapper: (T) -> R
  private lateinit var contramapper: (R) -> T

  fun withMap(mapper: (T) -> R): SerializerExtender<T, R> { this.mapper = mapper; return this }

  fun withContramap(contramapper: (R) -> T): SerializerExtender<T, R> { this.contramapper = contramapper; return this }

  fun build(descriptorName: String? = null): KSerializer<R> =
    object: KSerializer<R> {
      override val descriptor: SerialDescriptor =
        when {
          descriptorName != null && original.descriptor.kind is PrimitiveKind ->
            PrimitiveSerialDescriptor(descriptorName, original.descriptor.kind as PrimitiveKind)
          descriptorName != null && original.descriptor.kind !is PrimitiveKind ->
            throw IllegalStateException("Can only copy a primitive descriptor kind but kind is ${original.descriptor.kind}")
          else ->
            original.descriptor
        }

      override fun deserialize(decoder: Decoder): R = mapper(original.deserialize(decoder))
      override fun serialize(encoder: Encoder, value: R) = original.serialize(encoder, contramapper(value))
    }
}


class SerializerExtenderPrepared<T>(private val original: KSerializer<T>) {
  fun <R> to() = SerializerExtender<T, R>(original)
}

val <T> KSerializer<T>.extend get() = SerializerExtenderPrepared<T>(this)
