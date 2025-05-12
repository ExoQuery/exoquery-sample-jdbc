package io.exoquery.example

import io.exoquery.capture
import io.exoquery.controller.jdbc.JdbcControllers
import io.exoquery.controller.runActions
import io.exoquery.sql.PostgresDialect
import io.zonky.test.db.postgres.embedded.EmbeddedPostgres


fun insertAction() {
  data class Person(val id: Int, val name: String, val age: Int)

  val nameVal = "Joe"
  val ageVal = 123

  val q =
    capture {
      insert<Person> { set(name to param(nameVal), age to param(ageVal)) }
    }


  println(q.build<PostgresDialect>().value)
}

fun insertActionSetParams() {
  data class Person(val id: Int, val name: String, val age: Int)

  val p = Person(1, "Joe", 123)
  val q =
    capture {
      insert<Person> { setParams(p) }
    }

  println(q.build<PostgresDialect>().value)
}

fun insertWithExcluding() {
  data class Person(val id: Int, val name: String, val age: Int)

  val p = Person(1, "Joe", 123)
  val q =
    capture {
      insert<Person> { setParams(p).excluding(id) }
    }

  println(q.build<PostgresDialect>().value)
}

fun insertWithExcludingAndReturning() {
  data class Person(val id: Int, val name: String, val age: Int)

  val p = Person(1, "Joe", 123)
  val q =
    capture {
      insert<Person> { setParams(p).excluding(id) }.returning { p -> p.id }
    }

  println(q.build<PostgresDialect>())
}

fun insertWithExcludingAndReturningComplex() {
  data class Person(val id: Int, val name: String, val age: Int)
  data class Output(val id: Int, val name: String)

  val p = Person(1, "Joe", 123)
  val q =
    capture {
      insert<Person> { setParams(p).excluding(id) }.returning { p -> Output(p.id, p.name) }
    }

  println(q.build<PostgresDialect>())
}

suspend fun insertWithExcludingAndReturningComplexExpressions() {
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

  data class Person(val id: Int, val name: String, val email: String)
  data class Output(val id: Int, val name: String)

  val p = Person(1, "Joe", "joe@someplace.com")
  val q =
    capture {
      insert<Person> { setParams(p).excluding(id) }.returning { p -> Output(p.id, p.name + "-" + p.email) }
    }

  println(q.build<PostgresDialect>())
}

fun insertUpsert() {
  data class Person(val id: Int, val name: String, val age: Int)
  data class Output(val id: Int, val name: String)

  val p = Person(1, "Joe", 123)
  val q =
    capture {
      insert<Person> {
        setParams(p).onConflictUpdate(id) { excluding ->
          set(name to excluding.name)
        }
      }
    }
  println(q.build<PostgresDialect>())
}

fun insertUpsertComplex() {
  data class Person(val id: Int, val name: String, val age: Int)
  data class Output(val id: Int, val name: String)

  val p = Person(1, "Joe", 123)
  val q =
    capture {
      insert<Person> {
        setParams(p).onConflictUpdate(id) { excluding ->
          set(name to name + "-" + excluding.name)
        }
      }
    }
  println(q.build<PostgresDialect>())
}

fun insertUpsertIgnore() {
  data class Person(val id: Int, val name: String, val age: Int)
  data class Output(val id: Int, val name: String)

  val p = Person(1, "Joe", 123)
  val q =
    capture {
      insert<Person> {
        setParams(p).onConflictIgnore(id)
      }
    }
  println(q.build<PostgresDialect>())
}

fun updateAction() {
  data class Person(val id: Int, val name: String, val age: Int)

  val nameVal = "Joe"
  val ageVal = 123
  val someParam = 123

  val q =
    capture {
      update<Person> { set(name to param(nameVal), age to param(ageVal)) }
        .where { id == param(someParam) }
    }

  println(q.build<PostgresDialect>().value)
}

fun updateSetParams() {
  data class Person(val id: Int, val name: String, val age: Int)

  val p = Person(1, "Joe", 123)
  val q =
    capture {
      delete<Person>().where { id == param(p.id) }
    }

  println(q.build<PostgresDialect>().value)
}


fun batchInsert() {
  data class Person(val id: Int, val name: String, val age: Int)

  val people: Sequence<Person> =
    listOf(
      Person(1, "Joe", 123),
      Person(2, "Jim", 456),
      Person(3, "Jack", 789)
    ).asSequence()

  val q =
    capture.batch(people) { p ->
      insert<Person> { setParams(p).excluding(id) }
    }

  println(q.build<PostgresDialect>().value)
}
