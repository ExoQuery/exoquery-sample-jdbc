package io.exoquery.example

import io.exoquery.capture
import io.exoquery.capture.invoke
import io.exoquery.controller.jdbc.JdbcControllers
import io.exoquery.controller.jdbc.fromConfig
import io.exoquery.controller.runActions
import io.exoquery.jdbc.runOn
import io.zonky.test.db.postgres.embedded.EmbeddedPostgres
//import io.zonky.test.db.postgres.embedded.EmbeddedPostgres
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import javax.sql.DataSource

@Serializable
data class Person(val id: Int, val name: String, val age: Int)
data class Address(val ownerId: Int, val street: String, val zip: String)

suspend fun makeEmbeddedPostgresController(): JdbcControllers.Postgres {
  val postgres = EmbeddedPostgres.start()
  val ds: DataSource = postgres.postgresDatabase
  val query = capture {
    Table<Person>().filter { p -> p.name == "Ioffe" }
  }
  val postgresController = JdbcControllers.Postgres(ds)

  postgresController.runActions(
    """
      CREATE TABLE Person (
        id SERIAL PRIMARY KEY,
        firstName VARCHAR(255),
        lastName VARCHAR(255),
        age INT
      );
      INSERT INTO Person (firstName, lastName, age) VALUES
        ('Leib', 'Laffe', 19),
        ('Leah', 'Laffe', 17),
        ('Marina', 'Laffe', 47),
        ('Karina', 'Taffe', 37)
      """.trimIndent()
  )

  return postgresController
}

suspend fun makeLocalPostgresController(): JdbcControllers.Postgres =
  JdbcControllers.Postgres.fromConfig("testPostgresDB")

fun main() = runBlocking {

  //val controller = makeLocalPostgresController()
  val controller = makeEmbeddedPostgresController()

  val query = capture {
    Table<Person>().filter { p -> p.name == "Laffe" }
  }
  val output = query.buildFor.Postgres().runOn(controller)
  println(output)
}
