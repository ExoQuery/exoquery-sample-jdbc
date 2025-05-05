package io.exoquery.example

import io.exoquery.Ord
import io.exoquery.capture
import io.exoquery.capture.invoke

fun singleColumns() {
  val people = capture { Table<Person>() }
  val addresses = capture { Table<Address>() }
  val robots = capture { Table<Robot>() }
  val cars = capture { Table<Car>() }

  val q =
    capture.select {
      val p = from (people)
      val a = join (addresses) { a -> p.id == a.ownerId }
      p.name to a.street
    }

  println(q.buildFor.Postgres().value)
}

fun wholeRecord() {
  val people = capture { Table<Person>() }
  val addresses = capture { Table<Address>() }
  val robots = capture { Table<Robot>() }
  val cars = capture { Table<Car>() }

  val q =
    capture.select {
      val p = from (people)
      val a = join (addresses) { a -> p.id == a.ownerId }
      p to a
    }

  println(q.buildFor.Postgres().value)
}

fun whereClause() {
  val people = capture { Table<Person>() }
  val addresses = capture { Table<Address>() }
  val robots = capture { Table<Robot>() }
  val cars = capture { Table<Car>() }

  val q =
    capture.select {
      val p = from (people)
      val a = join (addresses) { a -> p.id == a.ownerId }
      where { p.age > 30 }
      p to a
    }

  println(q.buildFor.Postgres().value)
}

fun groupByClause() {
  val people = capture { Table<Person>() }
  val addresses = capture { Table<Address>() }
  val robots = capture { Table<Robot>() }
  val cars = capture { Table<Car>() }

  val q =
    capture.select {
      val p = from (people)
      val a = join (addresses) { a -> p.id == a.ownerId }
      where { p.age > 30 }
      groupBy(p.name)
      Triple(p.name, avg(p.age), count(a.zip))
    }

  println(q.buildFor.Postgres().value)
}

fun groupByClauseMultiple() {
  val people = capture { Table<Person>() }
  val addresses = capture { Table<Address>() }
  val robots = capture { Table<Robot>() }
  val cars = capture { Table<Car>() }

  val q =
    capture.select {
      val p = from (people)
      val a = join (addresses) { a -> p.id == a.ownerId }
      where { p.age > 30 }
      groupBy(p.name, p.age)
      Triple(p.name, p.age, count(a.zip))
    }

  println(q.buildFor.Postgres().value)
}

fun sortByClauseMultiple() {
  val people = capture { Table<Person>() }
  val addresses = capture { Table<Address>() }
  val robots = capture { Table<Robot>() }
  val cars = capture { Table<Car>() }

  val q =
    capture.select {
      val p = from (people)
      val a = join (addresses) { a -> p.id == a.ownerId }
      where { p.age > 30 }
      groupBy(p.name, p.age)
      sortBy(p.name to Ord.Asc, p.age to Ord.Desc)
      Triple(p.name, p.age, count(a.zip))
    }

  println(q.buildFor.Postgres().value)
}

fun plainSelect() {
  val people = capture { Table<Person>() }
  val addresses = capture { Table<Address>() }
  val robots = capture { Table<Robot>() }
  val cars = capture { Table<Car>() }

  val q =
    capture.select {
      val p = from(people)
      p
    }

  println(q.buildFor.Postgres().value)
}


fun main() {
  wholeRecord()
}