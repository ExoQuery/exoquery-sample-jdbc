package io.exoquery.example

import io.exoquery.capture
import io.exoquery.sql.PostgresDialect


val insertQuery =
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
    """

object Part2_DesugaringExample {
  val people = capture { Table<Person>() }
  val vips = capture { Table<Vip>() }
  val addresses = capture { Table<Address>() }
  val robots = capture { Table<Robot>() }
  val cars = capture { Table<Car>() }

  fun basicDesugaring() {
    val q =
      capture {
        people.flatMap { p ->
          internal.flatJoin(addresses) { a -> a.ownerId == p.id }.flatMap {
            internal.flatJoin(robots) { r -> r.ownerId == p.id }.flatMap {
              internal.flatJoin(cars) { c -> c.ownerId == p.id }.map { (a, r, c) ->
                Triple(p, a, Pair(r, c))
              }
            }
          }
        }
      }

    println(q.buildPretty<PostgresDialect>().value)
  }

  fun firstBlockNestedDesugaring() {
    val q =
      capture {
        people.flatMap { p ->
          internal.flatJoin(vips) { v ->
            v.id == p.id
          }.map { v -> p }
        }.flatMap { p ->
          internal.flatJoin(addresses) { a -> a.ownerId == p.id }.flatMap {
            internal.flatJoin(robots) { r -> r.ownerId == p.id }.flatMap {
              internal.flatJoin(cars) { c -> c.ownerId == p.id }.map { (a, r, c) ->
                Triple(p, a, Pair(r, c))
              }
            }
          }
        }
      }

    println(q.buildPretty<PostgresDialect>().value)

    val qDirect =
      capture.select {
        val p = from(
          capture.select {
            val p = from(people)
            val v = join(vips) { v -> v.id == p.id }
            p
          }
        )
        val a = join(addresses) { a -> a.ownerId == p.id }
        val r = join(robots) { r -> r.ownerId == p.id }
        val c = join(cars) { c -> c.ownerId == p.id }
        Triple(p, a, Pair(r, c))
      }

    println(qDirect.buildPretty<PostgresDialect>().value)
  }
}

fun main() {
  Part2_DesugaringExample.basicDesugaring()
  Part2_DesugaringExample.firstBlockNestedDesugaring()
}

