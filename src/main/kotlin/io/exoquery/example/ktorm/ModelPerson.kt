//package io.exoquery.example.ktorm
//
//import org.ktorm.schema.Table
//import org.ktorm.schema.int
//import org.ktorm.schema.varchar
//
//object Person : Table<Nothing>("person") {
//  val id = int("id").primaryKey()
//  val name = varchar("name")
//  val age = int("age")
//}
//
//object Vip : Table<Nothing>("vip") {
//  val id = int("id").primaryKey()
//  val membershipLevel = int("membership_level")
//}
//
//object Address : Table<Nothing>("address") {
//  val ownerId = int("owner_id").primaryKey()
//  val street = varchar("street")
//  val zip = int("zip")
//}
//
//object Robot : Table<Nothing>("robot") {
//  val ownerId = int("owner_id").primaryKey()
//  val model = varchar("model")
//}
//
//object Car : Table<Nothing>("car") {
//  val ownerId = int("owner_id").primaryKey()
//  val make = varchar("make")
//  val model = varchar("model")
//}
