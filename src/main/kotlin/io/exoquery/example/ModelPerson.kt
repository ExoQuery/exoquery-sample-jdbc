package io.exoquery.example

data class Person(val id: Int, val name: String, val age: Int)
data class Vip(val id: Int, val membershipLevel: Int)
data class Address(val ownerId: Int, val street: String, val city: String, val zip: Int)
data class Robot(val id: Int, val ownerId: Int, val model: String)
data class Car(val ownerId: Int, val make: String, val model: String)
