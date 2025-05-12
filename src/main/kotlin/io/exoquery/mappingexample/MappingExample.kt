package io.exoquery.mappingexample

import io.exoquery.annotation.CapturedFunction
import io.exoquery.capture
import io.exoquery.sql.PostgresDialect


data class Account(val name: String, val tag: String, val number: Int, val type: String)
data class Client(val alias: String, val code: String, val permission: String, val tag: String)


fun main() {
  @CapturedFunction
  fun condition(c: Client, a: Account) = capture.expression {
    when {
      c.code == "EV" ->
        a.number.toString()
      else ->
        a.number.toString() + c.alias.substring(0, 2)
    }
  }

  val query =
    capture.select {
      val c = from(Table<Client>())
      val a = join(Table<Account>()) { a -> a.tag == c.tag }
      Pair(
        c.alias,
        condition(c, a).use
      )
    }

  query.buildPretty<PostgresDialect>()
}
