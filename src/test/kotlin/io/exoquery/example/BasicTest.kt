package io.exoquery.example

import io.exoquery.capture
import io.exoquery.controller.jdbc.JdbcControllers
import io.exoquery.jdbc.runOn
import kotlinx.coroutines.runBlocking
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class BasicTest {
  lateinit var controller: JdbcControllers.Postgres

  @BeforeTest
  fun setup() = runBlocking {
    controller = makeEmbeddedPostgresController()
  }

  @Test
  fun test() = runBlocking {
    val query = capture {
      Table<Person>().filter { p -> p.lastName == "Laffe" }
    }
    val output = query.buildFor.Postgres().runOn(controller)
    assertEquals(
      output,
      listOf(
        Person(1, "Leib", "Laffe", 19),
        Person(2, "Leah", "Laffe", 17),
        Person(3, "Marina", "Laffe", 47)
      )
    )
  }
}
