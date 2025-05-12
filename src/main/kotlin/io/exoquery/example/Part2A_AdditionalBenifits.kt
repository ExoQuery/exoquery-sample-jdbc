package io.exoquery.example

import io.exoquery.*
import io.exoquery.annotation.CapturedFunction

fun main() {
  //@CapturedFunction
  //fun Person.joinAddresses() = capture {
  //  internal.flatJoin(Table<Address>()) { a -> a.ownerId ==  this@joinAddresses.id }
  //}

  val query = capture.select {
    val p = from(Table<Person>())
    val a = from(Table<Address>())
    where { p.id == a.ownerId }
    Pair(p, a)
  }

  query.buildFor.Postgres()

}