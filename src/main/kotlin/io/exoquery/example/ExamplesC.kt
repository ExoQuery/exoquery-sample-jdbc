package io.exoquery.example

import io.exoquery.annotation.ExoEntity
import io.exoquery.capture
import io.exoquery.controller.runActions
import io.exoquery.runOn

// From: https://stackoverflow.com/questions/64163627/handling-subquery-in-a-kotlin-exposed-framework
// SELECT FirstName, LastName,
//    (SELECT COUNT(O.Id)
//     FROM "Order" O
//     WHERE O.CustomerId = C.Id) AS OrderCount
// FROM Customer C;


data class Customer(val id: Int, val firstName: String, val lastName: String, val age: Int)
@ExoEntity(""""Order"""")
data class Order(val id: Int, val customerId: Int, val order: Int)

object CustomerOrderSchema {
  val schema =
    """
    create table Customer (
      id serial primary key,
      firstName varchar(255),
      lastName varchar(255),
      age int
    );
    
    create table "Order" (
      id serial primary key,
      customerId int,
      "order" int
    );
    
    insert into Customer (firstName, lastName, age) values
      ('Leib', 'Laffe', 19),
      ('Leah', 'Laffe', 17),
      ('Marina', 'Laffe', 47),
      ('Karina', 'Taffe', 37);
      
    insert into "Order" (customerId, "order") values
      (1, 1),
      (1, 2),
      (2, 3),
      (3, 4);
    """.trimIndent()
}

suspend fun subqueryExample_20250520() {
  val ctrl = makeEmbeddedPostgresController()
  ctrl.runActions(CustomerOrderSchema.schema)

  println("hello")


val q = capture.select {
  val c = from(Table<Customer>())
  Triple(
    c.firstName,
    c.lastName,
    capture {
      Table<Order>()
        .filter { o -> o.customerId == c.id }
        .map { o -> count(o.id) }
    }.value()
  )
}

println(q.buildFor.Postgres().runOn(ctrl))



}


suspend fun main() {
  subqueryExample_20250520()
}
