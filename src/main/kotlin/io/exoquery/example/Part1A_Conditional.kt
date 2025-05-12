package io.exoquery.example

import io.exoquery.*

object Part1A_conditional {

  data class ClientData(val alias: String, val orderPermission: String)

  val c = capture {
    Table<Pair<Client, Accounts>>().map { (c, a) ->
      Pair(
        c.alias,
        when {
          c.code == "EV" ->
            a.number.toString()
          else ->
            a.number.toString() + c.alias.substring(0, 2)
        }
      )
    }
  }

  fun run() {
    println(c.buildFor.Postgres().value)
  }
}

fun main() {
  Part1A_conditional.run()
}