package io.exoquery.example.exposed

import com.github.vertical_blank.sqlformatter.SqlFormatter
import io.zonky.test.db.postgres.embedded.EmbeddedPostgres
import org.jetbrains.exposed.sql.Case
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.QueryAlias
import org.jetbrains.exposed.sql.QueryBuilder
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.alias
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.concat
import org.jetbrains.exposed.sql.VarCharColumnType
import org.jetbrains.exposed.sql.castTo
import org.jetbrains.exposed.sql.stringLiteral
import org.jetbrains.exposed.sql.substring
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.unionAll

object Part1A_GiantQuery {

  val merchantClientSubquery = MerchantClients
    .join(Registry, JoinType.INNER, onColumn = MerchantClients.alias, otherColumn = Registry.alias)
    .select(
      MerchantClients.alias,
      MerchantClients.code,
      MerchantClients.orderPermission,
      MerchantClients.accountTag
    )
    .where {
      (Registry.market eq "us") and (Registry.recordType eq "M")
    }
    .withDistinct()


  val serviceClientSubquery = ServiceClients
    .join(Registry, JoinType.INNER, onColumn = ServiceClients.alias, otherColumn = Registry.alias)
    .join(Partnerships, JoinType.INNER, onColumn = ServiceClients.partnershipFk, otherColumn = Partnerships.id)
    .select(
      ServiceClients.alias,
      stringLiteral("EV").alias("code"),
      Partnerships.orderPermission,
      ServiceClients.accountTag
    )
    .where {
      (Registry.market eq "us") and (Registry.recordType eq "S")
    }
    .withDistinct()

  object UnionSubquery : Table("client") {
    val alias = varchar("alias", 255)
    val code = varchar("code", 255)
    val orderPermission = varchar("order_permission", 1)
    val accountTag = varchar("account_tag", 4)
  }



  val unionQuery =
    merchantClientSubquery
    .unionAll(serviceClientSubquery)
    .alias("client")

  val accountQuery =
    Accounts
      .join(AccountTypes, JoinType.INNER, onColumn = Accounts.type, otherColumn = AccountTypes.accountType)
      .join(DedicatedAccounts, JoinType.LEFT, onColumn = DedicatedAccounts.accountNumber, otherColumn = Accounts.number)
      .select(
        Accounts.name,
        Accounts.tag,
        Accounts.number,
        AccountTypes.mappingType
      ).alias("accountJoined")

  object AccountsSubquery : Table("accountJoined") {
    val name = varchar("name", 255)
    val tag = varchar("tag", 4)
    val number = integer("number")
    val alias = varchar("alias", 255)
    val mappingType = integer("mapping_type")
  }

  val bigQuery =
    unionQuery
      .join(accountQuery, JoinType.INNER, onColumn = UnionSubquery.alias, otherColumn = AccountsSubquery.alias)
      .select(
        AccountsSubquery.name,
        UnionSubquery.alias,
        Case().When(
          UnionSubquery.code eq stringLiteral("EV"),
          AccountsSubquery.number.castTo(VarCharColumnType(255))
        ).Else(
          concat(AccountsSubquery.number.castTo(VarCharColumnType(255)), UnionSubquery.alias.substring(0, 2))
        ).alias("OFFICIAL_IDENTITY"),
      )
      .where {
        (AccountsSubquery.mappingType eq 0) or
        ((AccountsSubquery.mappingType eq 2) and (AccountsSubquery.tag eq UnionSubquery.accountTag)) or
        ((AccountsSubquery.mappingType eq 1) and (AccountsSubquery.alias eq UnionSubquery.alias))
      }

  class UnionSubqueryAliased(alias: String) : Table(alias) {
    val alias = varchar("alias", 255)
    val code = varchar("code", 255)
    val orderPermission = varchar("order_permission", 1)
    val accountTag = varchar("account_tag", 4)
  }

  fun clientAccounts(client: QueryAlias) = run{
    val UnionSubquery = UnionSubqueryAliased(client.alias)
    client
      .join(accountQuery, JoinType.INNER, onColumn = UnionSubquery.alias, otherColumn = AccountsSubquery.alias)
      .select(
        AccountsSubquery.name,
        UnionSubquery.alias,
        Case().When(
          UnionSubquery.code eq stringLiteral("EV"),
          AccountsSubquery.number.castTo(VarCharColumnType(255))
        ).Else(
          concat(AccountsSubquery.number.castTo(VarCharColumnType(255)), UnionSubquery.alias.substring(0, 2))
        ).alias("OFFICIAL_IDENTITY"),
      )
      .where {
        (AccountsSubquery.mappingType eq 0) or
            ((AccountsSubquery.mappingType eq 2) and (AccountsSubquery.tag eq UnionSubquery.accountTag)) or
            ((AccountsSubquery.mappingType eq 1) and (AccountsSubquery.alias eq UnionSubquery.alias))
      }
  }


}


fun main() {
  val postgres = EmbeddedPostgres.start()
  val ds = postgres.postgresDatabase

  val db = Database.connect(ds)
  transaction(db) {
    //val rs = Part1A_GiantQuery.bigQuery.prepareSQL(QueryBuilder(false))

    val rs = Part1A_GiantQuery.clientAccounts(Part1A_GiantQuery.unionQuery).prepareSQL(QueryBuilder(false))

    println(SqlFormatter.format(rs))
  }
}