package io.exoquery.example

import io.exoquery.capture
import io.exoquery.runOn
import kotlinx.serialization.Serializable

object ExamplesB {
  val ctx = Common.postgresController

  @JvmInline
  @Serializable
  value class PersonId(val id: Int)
  @Serializable
  data class Person(val id: PersonId, val name: String, val age: Int)
  @Serializable
  data class Address(val ownerId: PersonId, val street: String, val zip: String)

  val myId = PersonId(1)
  val q =
    capture.select {
      val p = from(Table<Person>())
      val a = join(Table<Address>()) { p.id == it.ownerId }
      where { p.id == param(myId) }
      p to a
    }

  suspend fun valueExample_20250521() = q.buildFor.Postgres().runOn(ctx)
}

suspend fun main() {
  Common.setupDB(Common.postgresController)
  val result = ExamplesB.valueExample_20250521()
  println(result)
}
