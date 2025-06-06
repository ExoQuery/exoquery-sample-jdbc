package io.exoquery.example

import io.exoquery.Ord
import io.exoquery.SqlQuery
import io.exoquery.capture
import io.exoquery.runOn

object ExamplesD {


// You made me laugh out loud @sebi_io! Here's what a "Kotlin Typesafe DSL without diverging from core SQL concepts" actually looks like!




suspend fun main() {
val ctx = Common.postgresController


data class City(val id: Int, val name: String)
data class User(val id: Int, val name: String, val cityId: Int)

val q: SqlQuery<Pair<User, City>> =
  capture.select {
    val u = from(Table<User>())
    val c = join(Table<City>()) { u.cityId == it.id }
    where { c.name == "Copenhagen" }
    u to c
  }

q.buildFor.Postgres().runOn(ctx)
  .map { (u, c) -> println("${u.name} lives in ${c.name}") }



}


}



fun other() {

data class Person(val id: Int, val name: String, val age: Int)
data class Address(val ownerId: Int, val street: String, val zip: String)

val q =
  capture.select {
    val p = from(Table<Person>())
    val a = join(Table<Address>()) { p.id == it.ownerId }
    groupBy(p.name, a.zip)
    sortBy(p.name to Ord.Asc)
    where { p.age > 30 }
    Triple(p.name, a.zip, avg(p.age))
  }

println(q.buildFor.Postgres().value)

}
