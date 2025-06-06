package io.exoquery.example

import io.exoquery.annotation.CapturedFunction
import io.exoquery.capture



fun booleanLogicConversion_20250529() {

data class Client(val name: String, val isRegistered: Boolean)

val q =
  capture.select {
    val c = from(Table<Client>())
    where { c.isRegistered || c.name == "Joe" }
    Pair(c.name, c.isRegistered || c.name == "Joe")
  }

println(q.buildFor.Postgres().value)
// SELECT p.name FROM Client AS p WHERE p.isRegistered OR p.name = 'Joe'

println(q.buildFor.SqlServer().value)
// SELECT p.name FROM Client AS p WHERE p.isRegistered = 1 OR p.name = 'Joe'


}


fun dateTypesOutOfTheBox_20250529() {



data class Client(val name: String, val birthDate: kotlinx.datetime.LocalDate)
val minBirthday = kotlinx.datetime.LocalDate(2000, 1, 1)

val q =
  capture.select {
    val c = from(Table<Client>())
    where { c.birthDate < param(minBirthday) } // use paramCtx for java.time.*
    c.name
  }

println(q.buildFor.Postgres().value)


}



fun windowFunctions_20250530_A() {

data class Customer(val name: String, val age: Int, val membership: String)

val q =
  capture.select {
    val c = from(Table<Customer>())
    Pair(
      c.name,
      // Average age of customers in the same membership group, ordered by name
      over().partitionBy(c.membership).orderBy(c.name).avg(c.age)
    )
  }

println(q.buildFor.Postgres().value)
//> SELECT p.name, AVG(p.age) OVER (PARTITION BY p.membership ORDER BY p.name)
//  FROM Customer AS p



}




fun windowFunctions_20250530_B() {

data class Data(val name: String, val a: Int, val b: Int, val c: Int)


data class Customer(val name: String, val age: Int, val membership: String)
data class VipCustomer(val name: String, val statusAge: Int, val tier: Int)

@CapturedFunction
fun ntile(by: Customer, n: Int) = capture.expression {
  over()
    .partitionBy(by.membership)
    .orderBy(by.age)
    .frame(free("NTILE($n)").invoke<Int>())
}


@CapturedFunction
fun ntileByAge(by: String, age: Int, n: Int) = capture.expression {
  over()
    .partitionBy(by)
    .orderBy(age)
    .frame(free("NTILE($n)").invoke<Int>())
}





val ntile2 = capture.expression {
  { c: Customer, n: Int ->
    over()
      .partitionBy(c.membership)
      .orderBy(c.age)
      .sum(n)
  }
}

val q =
  capture.select {
    val c = from(Table<Customer>())
    Data(
      c.name,
      ntile(c, 5).use,
      ntile(c, 10).use,
      ntile(c, 20).use,
    )
  }

  /*
  ntileByAge(c.name, c.age, 5).use,
      ntileByAge(c.name, c.age, 10).use,
      ntileByAge(c.name, c.age, 20).use,
   */


println(q.buildFor.Postgres().value)

}


fun dateTypesOutOfTheBox_20250529_2() {

data class Client(val name: String, val birthDate: java.time.LocalDate)
val minBirthday = java.time.LocalDate.of(2000, 1, 1)
val q =
  capture.select {
    val c = from(Table<Client>())
    where { c.birthDate < paramCtx(minBirthday) }
    c.name
  }

println(q.buildFor.Postgres().value)

}

fun paramsWithCustomSerialization_20250529() {

}
