package io.exoquery.example

import io.exoquery.capture

fun correlatedExample1_20250520() {

  val q = capture.select {
    val p = from(Table<Person>())
    where { p.age > Table<Person>().map { it.age }.avg() }
    Pair(p.name, p.age)
  }

  println(q.buildFor.Postgres().value)
}

fun correlatedExample2_20250520() {
  val q = capture.select {
    val p = from(Table<Person>())
    where { p.age >
        Table<Person>().map { min(it.age) + avg(it.age) }.value()
    }
    Pair(p.name, p.age)
  }

  println(q.buildFor.Postgres().value)
}

fun nullabilityExample_20250521() {
  val q = capture.select {
    val p: Person   = from(Table<Person>())
    val a: Address? = joinLeft(Table<Address>()) { p.id == it.ownerId }
    Triple(p.name, a?.street ?: "defaultStreet", a?.zip ?: "defaultZip")
  }

  println(q.buildFor.Postgres().value)
}

fun main() {
}
