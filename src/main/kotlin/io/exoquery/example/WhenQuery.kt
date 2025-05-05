package io.exoquery.example

import io.exoquery.capture
import io.exoquery.sql.PostgresDialect

//SELECT DISTINCT
//  account.name,
//      alias,
//  CASE WHEN code = 'EV'
//    THEN cast(account.number AS VARCHAR)
//    ELSE cast(account.number AS VARCHAR) + substring(alias, 1, 2) END AS OFFICIAL_IDENTITY,
//  CASE WHEN order_permission IN ('A', 'S')
//    THEN 'ST' ELSE 'ENH' END

// mc.alias, mc.code, order_permission, mc.account_tag




fun main() {
  data class Client(val alias: String, val code: String, val orderPermission: String, val accountTag: String)

  data class ClientData(val alias: String, val orderPermission: String)

  val q =
    capture {
      Table<Client>().map { c ->
        ClientData(
          c.alias,
          when (c.orderPermission) {
            "A", "S" -> "ST"
            else -> "ENH"
          }
        )
      }
    }


//    capture {
//      Table<Pair<Client, Account>>().map { (c, a) ->
//        ClientAccount(
//          a.name,
//          c.alias,
//          when {
//            c.code == "EV" -> a.number.toString()
//            else -> a.number.toString() + c.alias.take(2)
//          },
//          when (c.orderPermission) {
//            'A', 'S' -> "ST"
//            else -> "ENH"
//          }
//        )
//      }
//    }
  println(q.buildPretty<PostgresDialect>().value)
}