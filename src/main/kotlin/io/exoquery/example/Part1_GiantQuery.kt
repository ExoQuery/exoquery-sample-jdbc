package io.exoquery.example

import io.exoquery.capture
import io.exoquery.sql.PostgresDialect

//create table ACCOUNT_TYPES (
//ACCOUNT_TYPE varchar(255) PRIMARY KEY,
//MAPPING_TYPE int) GO

data class AccountTypes(val accountType: String, val mappingType: Int)

///*not null*/
//create table ACCOUNTS (
//NAME varchar(255),
//TAG varchar(4),
//NUMBER int PRIMARY KEY,
//TYPE varchar(255),
//FOREIGN KEY (TYPE) REFERENCES ACCOUNT_TYPES(ACCOUNT_TYPE)
//) GO

data class Accounts(val name: String, val tag: String, val number: Int, val type: String)


//create table ORDER_PERMISSION_TYPES (
//TYPE varchar(1) PRIMARY KEY,
//DESCRIPTION varchar(255)
//) GO

data class OrderPermissionTypes(val type: String, val description: String)


//create table PARTNERSHIPS (
//ID int PRIMARY KEY,
//ORDER_PERMISSION varchar(1),
//DESCRIPTION varchar(255),
//FOREIGN KEY (ORDER_PERMISSION) REFERENCES ORDER_PERMISSION_TYPES(TYPE)
//) GO

data class Partnerships(val id: Int, val orderPermission: String, val description: String)

//create table REGISTRY (
//ALIAS varchar(255) PRIMARY KEY,
//RECORD_TYPE varchar(1),
//MARKET varchar(255),
//DESCRIPTION varchar(255)
//) GO

data class Registry(val alias: String, val recordType: String, val market: String, val description: String)

//create table MERCHANT_CLIENTS (
//ALIAS varchar(255),
//CODE varchar(255),
//ORDER_PERMISSION varchar(1),
//ACCOUNT_TAG varchar(4),
//FOREIGN KEY (ORDER_PERMISSION) REFERENCES ORDER_PERMISSION_TYPES(TYPE)
//) GO

data class MerchantClients(val alias: String, val code: String, val orderPermission: String, val accountTag: String)

//create table SERVICE_CLIENTS (
//ALIAS varchar(255),
//PARTNERSHIP_FK int,
//ACCOUNT_TAG varchar(4),
//FOREIGN KEY (PARTNERSHIP_FK) REFERENCES PARTNERSHIPS(ID)
//) GO

data class ServiceClients(val alias: String, val partnershipFk: Int, val accountTag: String)

//create table DEDICATED_ACCOUNTS (
//ACCOUNT_NUMBER int,
//CLIENT_ALIAS varchar(255),
//FOREIGN KEY (ACCOUNT_NUMBER) REFERENCES ACCOUNTS(NUMBER)
//) GO

data class ClientAccount(val name: String, val alias: String, val officialIdentity: String, val orderPermission: String)

data class DedicatedAccounts(val accountNumber: Int, val clientAlias: String)

// case class Client(alias: Rep[Option[String]], code: Rep[Option[String]], permission: Rep[Option[Char]], tag: Rep[Option[String]])
data class Client(val alias: String, val code: String, val permission: String, val tag: String)



object Clients {
  //SELECT DISTINCT
  //merchantClient.alias,
  //merchantClient.code,
  //order_permission,
  //merchantClient.account_tag
  //FROM MERCHANT_CLIENTS merchantClient
  //JOIN REGISTRY entry ON entry.alias = merchantClient.alias
  //WHERE entry.market = 'us' AND entry.record_type = 'M'



//  SELECT DISTINCT
//  serviceClient.alias,
//  'EV' AS code,
//  partnership.order_permission,
//  serviceClient.account_tag
//  FROM SERVICE_CLIENTS serviceClient
//  JOIN REGISTRY entry ON entry.alias = serviceClient.alias and entry.record_type = 'S' AND entry.market = 'us'
//  JOIN PARTNERSHIPS partnership ON partnership.id = serviceClient.partnership_fk
//SELECT DISTINCT
//  account.name,
//      alias,
//  CASE WHEN code = 'EV'
//    THEN cast(account.number AS VARCHAR)
//    ELSE cast(account.number AS VARCHAR) + substring(alias, 1, 2) END AS OFFICIAL_IDENTITY,
//  CASE WHEN order_permission IN ('A', 'S')
//    THEN 'ST' ELSE 'ENH' END
//FROM  ALL_CLIENTS client
//  INNER JOIN    (
//      dbo.ACCOUNTS account
//      INNER JOIN ACCOUNT_TYPES accountType ON account.type = accountType.account_type
//      LEFT JOIN DEDICATED_ACCOUNTS dedicated ON dedicated.account_number = account.number
//    )
//    ON   (accountType.mapping_type = 0 )
//         OR  (accountType.mapping_type = 2 AND account.tag = client.account_tag)
//         OR  (accountType.mapping_type = 1 AND dedicated.client_alias = client.alias)
// GO

  // TODO make this into a captured function
  // TODO I think when we remove the .nested it's more efficient, look into this
  // TODO toString should be supported!!, also String.take(2) should be supported

  fun merchant() =
    capture.select {
      val merchantClient = from(Table<MerchantClients>())
      val entry = join(Table<Registry>()) { entry -> merchantClient.alias == entry.alias }
      where { entry.market == "us" && entry.recordType == "M" }
      Client(
        merchantClient.alias,
        merchantClient.code,
        merchantClient.orderPermission,
        merchantClient.accountTag
      )
    }
  fun service() =
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
  fun allClients() = capture { service() union merchant() }
  fun giantQuery() = capture.select {
    val client = from(allClients().nested())
    val (account, accountType, dedicated) = join(
      capture.select {
        val account = from(Table<Accounts>())
        val accountType = join(Table<AccountTypes>()) { at -> account.type == at.accountType }
        val dedicated = joinLeft(Table<DedicatedAccounts>()) { d -> d.accountNumber == account.number }
        Triple(account, accountType, dedicated)
      }) { (account, accountType, dedicated) ->
      (accountType.mappingType == 0) ||
      (accountType.mappingType == 2 && (account.tag == client.tag)) ||
      (accountType.mappingType == 1 && (dedicated?.let { it.clientAlias == client.alias } ?: false))
    }
    ClientAccount(
      account.name,
      client.alias,
      when (client.code) {
        "EV" -> account.number as String
        else -> (account.number as String) + client.alias.sql.left(2)
      },
      when (client.permission) {
        "A", "S" -> "ST"
        else -> "ENH"
      }
    )
  }
}

fun main() {
  println(Clients.giantQuery().buildPretty<PostgresDialect>().value)
}

