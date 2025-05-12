package io.exoquery.example

import io.exoquery.Ord
import io.exoquery.capture
import io.exoquery.sql.PostgresDialect
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.serializer

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

fun leftJoins1() {
  val people = capture { Table<Person>() }
  val addresses = capture { Table<Address>() }
  val robots = capture { Table<Robot>() }
  val cars = capture { Table<Car>() }

  val q =
    capture.select {
      val p = from (people)
      val a = joinLeft (addresses) { a -> p.id == a.ownerId }
      p to a?.street
    }

  println(q.build<PostgresDialect>("leftJoins1").value)
}

fun leftJoins2() {
  val people = capture { Table<Person>() }
  val addresses = capture { Table<Address>() }
  val robots = capture { Table<Robot>() }
  val cars = capture { Table<Car>() }

  val q =
    capture.select {
      val p = from (people)
      val a = joinLeft (addresses) { a -> p.id == a.ownerId }
      p to a?.let { it.street to it.zip }
    }

  println(q.buildFor.Postgres("leftJoins2").value)
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


fun tableWithFilter() {
  val query =
    capture {
      Table<Person>().filter { p -> p.name == "Joe" }
    }

  //val people: List<Person> = query.buildFor.Postgres().runOn(db)
}

fun tableWithFilterCorrelated() {
  val query =
    capture {
      Table<Person>().filter { p -> p.age > Table<Person>().map { it.age }.avg() }
    }

  println(query.buildFor.Postgres().value)
}

fun tableWithMap1() {
  val query =
    capture {
      Table<Person>().map { p -> Pair(p.name, p.age) }
    }
  println(query.buildFor.Postgres().value)
}

fun tableWithMap2() {
  val query =
    capture {
      Table<Person>().map { p -> Pair(p.name, p.age) }.distinct()
    }
  println(query.buildFor.Postgres().value)
}

fun tableWithMap3() {
  val query =
    capture {
      Table<Person>().map { p -> Pair(count(p.name), avg(p.age)) }
    }

  println(query.buildFor.Postgres().value)
}

fun tableWithTakeDrop() {
  val query =
    capture {
      Table<Person>().take(5).drop(1)
    }

  println(query.buildFor.Postgres().value)
}

fun tableWithTakeDrop2() {
  val query =
    capture {
      Table<Person>().drop(1).take(5)
    }

  println(query.buildFor.Postgres().value)
}

fun tableUnions() {
  data class IdAndName(val id: Int, val name: String)
  val query =
    capture {
      Table<Person>().map { p -> IdAndName(p.id, p.name) } union
        Table<Robot>().map { r -> IdAndName(r.id, r.model) }
    }

  println(query.buildFor.Postgres().value)
}

fun tableUnionsComposed() {
  val peopleInNewYork =
    capture.select {
      val p = from(Table<Person>())
      val a = join(Table<Address>()) { a -> p.id == a.ownerId }
      where { a.city == "New York" }
      p
    }

  val tSeriesRobots =
    capture { Table<Robot>().filter { r -> r.model.substring(0, 2) == "T-" } }

  data class IdAndName(val id: Int, val name: String)
  val query =
    capture {
      peopleInNewYork.map { p -> IdAndName(p.id, p.name) } union
          tSeriesRobots.map { r -> IdAndName(r.id, r.model) }
    }
}


// expressions
// splicing an expression
// lambdas in expressions
// captured functions

// parameters
// kotlinx serialization
// serializing
// deserializing

// insert
// update
// delete
// batch

// free




fun main() {
  wholeRecord()
}