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

fun main() {
  val people = capture { Table<Person>() }
  val addresses = capture { Table<Address>() }
  val robots = capture { Table<Robot>() }
  val cars = capture { Table<Car>() }

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
