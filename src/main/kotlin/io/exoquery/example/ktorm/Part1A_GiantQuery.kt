//package io.exoquery.example.ktorm
//
//import io.zonky.test.db.postgres.embedded.EmbeddedPostgres
//import org.ktorm.database.Database
//import org.ktorm.dsl.Query
//import org.ktorm.dsl.and
//import org.ktorm.dsl.eq
//import org.ktorm.dsl.toLong
//import org.ktorm.dsl.from
//import org.ktorm.dsl.innerJoin
//import org.ktorm.dsl.selectDistinct
//import org.ktorm.dsl.union
//import org.ktorm.dsl.where
//import org.ktorm.expression.ArgumentExpression
//import org.ktorm.schema.SqlType
//import org.ktorm.schema.varchar
//
//object Clients {
//
//  val postgres = EmbeddedPostgres.start()
//  val ds = postgres.postgresDatabase
//
//  val database = Database.connect(ds)
//
////MERCAHNT_CLIENT_SUBQUERY =
////SELECT DISTINCT
////merchantClient.alias,
////merchantClient.code,
////order_permission,
////merchantClient.account_tag
////FROM MERCHANT_CLIENTS merchantClient
////JOIN REGISTRY entry ON entry.alias = merchantClient.alias
////WHERE entry.market = 'us' AND entry.record_type = 'M'
//
//  val merchantClientSubquery = database
//    .from(MerchantClients)
//    .innerJoin(Registry, on = Registry.aliasCol eq MerchantClients.aliasCol)
//    .selectDistinct(
//      MerchantClients.aliasCol,
//      MerchantClients.code,
//      MerchantClients.orderPermission,
//      MerchantClients.accountTag
//    )
//    .where {
//      (Registry.market eq "us") and (Registry.recordType eq "M")
//    }
//
////SERVICE_CLIENT_SUBQUERY =
////SELECT DISTINCT
////  serviceClient.alias,
////  'EV' AS code,
////  partnership.order_permission,
////  serviceClient.account_tag
////  FROM SERVICE_CLIENTS serviceClient
////  JOIN REGISTRY entry ON entry.alias = serviceClient.alias and entry.record_type = 'S' AND entry.market = 'us'
////  JOIN PARTNERSHIPS partnership ON partnership.id = serviceClient.partnership_fk
//
//
//  val serviceClientSubquery = database
//    .from(ServiceClients)
//    .innerJoin(Registry, on = (Registry.aliasCol eq ServiceClients.aliasCol) and (Registry.recordType eq "S") and (Registry.market eq "us"))
//    .innerJoin(Partnerships, on = Partnerships.id eq ServiceClients.partnershipFk)
//    .selectDistinct(
//      ServiceClients.aliasCol,
//      ArgumentExpression("EV", SqlType.of<String>()!!).aliased("code"),
//      Partnerships.orderPermission,
//      ServiceClients.accountTag
//    )
//
////  object MerchantClients : org.ktorm.schema.Table<Nothing>("merchant_clients") {
////    val aliasCol = varchar("alias").primaryKey()
////    val code = varchar("code")
////    val orderPermission = varchar("order_permission")
////    val accountTag = varchar("account_tag")
////  }
//
//
//
//  val clientSubquery: Query =
//    merchantClientSubquery.union(serviceClientSubquery)
//
//
//
//
//
//
//
//
////SELECT DISTINCT
////  account.name,
////      alias,
////  CASE WHEN code = 'EV'
////    THEN cast(account.number AS VARCHAR)
////    ELSE cast(account.number AS VARCHAR) + substring(alias, 1, 2) END AS OFFICIAL_IDENTITY,
////  CASE WHEN order_permission IN ('A', 'S')
////    THEN 'ST' ELSE 'ENH' END
////FROM  (MERCAHNT_CLIENT_SUBQUERY UNION SERVICE_CLIENT_SUBQUERY) client
////  INNER JOIN
////      dbo.ACCOUNTS account
////      INNER JOIN ACCOUNT_TYPES accountType ON account.type = accountType.account_type
////      LEFT JOIN DEDICATED_ACCOUNTS dedicated ON dedicated.account_number = account.number
////    )
////    ON   (accountType.mapping_type = 0 )
////         OR  (accountType.mapping_type = 2 AND account.tag = client.account_tag)
////         OR  (accountType.mapping_type = 1 AND dedicated.client_alias = client.alias)
//
////  val fullQuery = database
////    .from(clientSubquery)
////    .innerJoin(Accounts, on =
////      (AccountTypes.mappingType eq 0) or
////          ((AccountTypes.mappingType eq 2) and (Accounts.tag eq clientSubquery["account_tag"])) or
////          ((AccountTypes.mappingType eq 1) and (DedicatedAccounts.clientAlias eq clientSubquery["alias"]))
////    )
////    .innerJoin(AccountTypes, on = Accounts[Accounts.number] eq AccountTypes.accountType) // Adjust join condition
////    .leftJoin(DedicatedAccounts, on = DedicatedAccounts.accountNumber eq Accounts.number)
////    .selectDistinct(
////      Accounts.name,
////      clientSubquery["alias"],
////      caseWhen {
////        clientSubquery["code"] eq "EV" then cast(Accounts.number, VarcharSqlType)
////        elseExpr(
////          concat(
////            cast(Accounts.number, VarcharSqlType),
////            substring(clientSubquery["alias"] as Expression<String>, 1, 2)
////          )
////        )
////      }.aliased("OFFICIAL_IDENTITY"),
////      caseWhen {
////        (clientSubquery["order_permission"] inList listOf("A", "S")) then stringLiteral("ST")
////        elseExpr(stringLiteral("ENH"))
////      }
////    )
//
//}
//
//fun main() {
//  println(Clients.clientSubquery.sql)
//}