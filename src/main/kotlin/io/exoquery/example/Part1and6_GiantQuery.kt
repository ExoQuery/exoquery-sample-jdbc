package io.exoquery.example

import io.exoquery.SqlQuery
import io.exoquery.annotation.CapturedFunction
import io.exoquery.capture
import io.exoquery.controller.jdbc.JdbcControllers
import io.exoquery.runOn
import io.exoquery.sql.PostgresDialect
import io.zonky.test.db.postgres.embedded.EmbeddedPostgres


object Part1and6_GiantQuery {

  // TODO make this into a captured function
  // TODO I think when we remove the .nested it's more efficient, look into this
  // TODO toString should be supported!!, also String.take(2) should be supported

  fun merchantClients() =
    capture.select {
      val merchantClient = from(Table<MerchantClients>())
      val entry = join(Table<Registry>()) { entry -> merchantClient.alias == entry.alias }
      where { entry.market == "us" && entry.recordType == "M" }
      Client(
        merchantClient.alias,
        merchantClient.code,
        merchantClient.permission,
        merchantClient.tag
      )
    }
  fun serviceClients() =
    capture.select {
      val serviceClient = from(Table<ServiceClients>())
      val entry = join(Table<Registry>()) { entry -> serviceClient.alias == entry.alias }
      val partnership = join(Table<Partnerships>()) { partnership -> partnership.id == serviceClient.partnershipFk }
      where { entry.market == "us" && entry.recordType == "S" }
      Client(
        serviceClient.alias,
        "EV", // hardcoded code as per the original query
        partnership.orderPermission,
        serviceClient.accountTag
      )
    }
  fun allClients(): SqlQuery<Client> = capture {
    merchantClients() unionAll serviceClients()
  }

  @CapturedFunction
  fun clientAccounts(clients: SqlQuery<Client>) = capture.select {
    val client = from(clients)
    val (account, accountType, dedicated) = join(
      capture.select {
        val account = from(Table<Accounts>())
        val accountType = join(Table<AccountTypes>()) { at -> account.type == at.accountType }
        val dedicated = joinLeft(Table<DedicatedAccounts>()) { d -> d.accountNumber == account.number }
        Triple(account, accountType, dedicated)
      }) { (account, accountType, dedicated) ->
      (accountType.mappingType == 0) ||
      (accountType.mappingType == 2 && (account.tag == client.tag)) ||
      (accountType.mappingType == 1 && (dedicated?.clientAlias == client.alias))
    }
    ClientAccount(
      account.name,
      client.alias,
      when (client.code) {
        "EV" -> account.number.toString()
        else -> account.number.toString() + client.alias.substring(1, 2)
      },
      when (client.permission) {
        "A", "S" -> "ST"
        else -> "ENH"
      }
    )
  }

  suspend fun run() {
    val query = capture {
      clientAccounts(allClients()).distinct()
    }
    query.buildPretty<PostgresDialect>().value

    val postgres = EmbeddedPostgres.start()
    val ds = postgres.postgresDatabase
    val ctx = JdbcControllers.Postgres(ds)

    createSchema(ctx)

    val output = GiantQuerySql.buildFor.Postgres().runOn(ctx)
    //println(output.map { it.toString() + "\n" })

    val output2 = query.buildFor.Postgres().runOn(ctx)
    //println(output2.map { it.toString() + "\n" })

    println(output == output2)
  }
}

suspend fun main() {
  val query = Part1and6_GiantQuery.run()
  println(query)
}

