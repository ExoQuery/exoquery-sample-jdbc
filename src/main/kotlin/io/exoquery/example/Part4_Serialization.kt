package io.exoquery.example

import io.exoquery.annotation.ExoField
import io.exoquery.annotation.ExoValue
import io.exoquery.capture
import io.exoquery.controller.jdbc.JdbcControllers
import io.exoquery.controller.runActions
import io.exoquery.jdbc.runOn
import io.zonky.test.db.postgres.embedded.EmbeddedPostgres
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.serializer

// CAN'T do 'data class' here or will try to do it structurally
//@JvmInline
@ExoValue
data class Email(val value: String)

object EmailSerializer : KSerializer<Email> {
  override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Email", PrimitiveKind.STRING)
  override fun serialize(encoder: Encoder, value: Email) = encoder.encodeString(value.value)
  override fun deserialize(decoder: Decoder) = Email(decoder.decodeString())
}

open class SerializerWrapper<T>(private val original: KSerializer<T>): KSerializer<T> {
  override val descriptor: SerialDescriptor = original.descriptor
  override fun serialize(encoder: Encoder, value: T) = original.serialize(encoder, value)
  override fun deserialize(decoder: Decoder): T = original.deserialize(decoder)
}

val emailSerializer: KSerializer<Email> =
  serializer<String>().extend.to<Email>()
    .withMap { Email(it) }
    .withContramap { it.value }
    .build("Email")

object EmailSerializer2 : SerializerWrapper<Email>(
  serializer<String>().extend.to<Email>()
    .withMap { Email(it) }
    .withContramap { it.value }
    .build("Email")
)

open class SerializerFrom<T, R>(
  original: KSerializer<T>,
  private val mapper: (T) -> R,
  private val contramapper: (R) -> T,
  private val descriptorName: String? = null
): KSerializer<R> {
  private val delegateSerializer = original.extend.to<R>()
    .withMap { mapper(it) }
    .withContramap { contramapper(it) }
    .build(descriptorName)

  override val descriptor: SerialDescriptor = delegateSerializer.descriptor
  override fun serialize(encoder: Encoder, value: R) = delegateSerializer.serialize(encoder, value)
  override fun deserialize(decoder: Decoder): R = delegateSerializer.deserialize(decoder)
}

object EmailSerializer3 : SerializerFrom<String, Email>(
  serializer(), { Email(it) }, { it.value }, "Email"
)



suspend fun getPeople() {

  @Serializable
  data class Person(val id: Int, val name: String,
    @Serializable(with = EmailSerializer::class)
    @ExoValue
    val email: Email
  )

  val postgres = EmbeddedPostgres.start()
  val ds = postgres.postgresDatabase
  val ctx = JdbcControllers.Postgres(ds)

  ctx.runActions(
    """
    CREATE TABLE person (
      id SERIAL PRIMARY KEY,
      name VARCHAR(50),
      email VARCHAR(50)
    );
    """.trimIndent()
  )

  val insert = listOf(
    Person(1, "Joe", Email("joe@someplace.com")),
    Person(2, "Joe", Email("joer@someplace.com")),
    Person(3, "Jim", Email("jim@someplace.com"))
  )
  val b = capture.batch(insert.asSequence()) {
    insert<Person> { setParams(it) }
  } // doing .buildFor.Postgres().runOn(ctx) here doesn't work
  b.buildFor.Postgres().runOn(ctx)



  val people =
    capture {
      Table<Person>().filter { p -> p.name == "Joe" }
    }

  val output = people.buildFor.Postgres().runOn(ctx)
  println(output)
}

suspend fun main() {
  getPeople()
}

