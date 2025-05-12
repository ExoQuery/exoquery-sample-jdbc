package io.exoquery.example.exposed

import org.jetbrains.exposed.sql.Table


//create table ACCOUNT_TYPES (
//ACCOUNT_TYPE varchar(255) PRIMARY KEY,
//MAPPING_TYPE int) GO

object AccountTypes : Table("ACCOUNT_TYPES") {
  val accountType = varchar("ACCOUNT_TYPE", 255) //.primaryKey()
  val mappingType = integer("MAPPING_TYPE")
}


//create table ACCOUNTS (
//NAME varchar(255),
//TAG varchar(4),
//NUMBER int PRIMARY KEY,
//TYPE varchar(255),
//FOREIGN KEY (TYPE) REFERENCES ACCOUNT_TYPES(ACCOUNT_TYPE)
//) GO


object Accounts : Table("ACCOUNTS") {
  val name = varchar("NAME", 255)
  val tag = varchar("TAG", 4)
  val number = integer("NUMBER") //.primaryKey()
  val type = reference("TYPE", AccountTypes.accountType)
}

//create table ORDER_PERMISSION_TYPES (
//TYPE varchar(1) PRIMARY KEY,
//DESCRIPTION varchar(255)
//) GO

object OrderPermissionTypes : Table("ORDER_PERMISSION_TYPES") {
  val type = varchar("TYPE", 1) //.primaryKey()
  val description = varchar("DESCRIPTION", 255)
}


//create table PARTNERSHIPS (
//ID int PRIMARY KEY,
//ORDER_PERMISSION varchar(1),
//DESCRIPTION varchar(255),
//FOREIGN KEY (ORDER_PERMISSION) REFERENCES ORDER_PERMISSION_TYPES(TYPE)
//) GO

object Partnerships : Table("PARTNERSHIPS") {
  val id = integer("ID") //.primaryKey()
  val orderPermission = reference("ORDER_PERMISSION", OrderPermissionTypes.type)
  val description = varchar("DESCRIPTION", 255)
}

//create table REGISTRY (
//ALIAS varchar(255) PRIMARY KEY,
//RECORD_TYPE varchar(1),
//MARKET varchar(255),
//DESCRIPTION varchar(255)
//) GO

object Registry : Table("REGISTRY") {
  val alias = varchar("ALIAS", 255) //.primaryKey()
  val recordType = varchar("RECORD_TYPE", 1)
  val market = varchar("MARKET", 255)
  val description = varchar("DESCRIPTION", 255)
}




//create table MERCHANT_CLIENTS (
//ALIAS varchar(255),
//CODE varchar(255),
//ORDER_PERMISSION varchar(1),
//ACCOUNT_TAG varchar(4),
//FOREIGN KEY (ORDER_PERMISSION) REFERENCES ORDER_PERMISSION_TYPES(TYPE)
//) GO

object MerchantClients : Table("MERCHANT_CLIENTS") {
  val alias = varchar("ALIAS", 255)
  val code = varchar("CODE", 255)
  val orderPermission = reference("ORDER_PERMISSION", OrderPermissionTypes.type)
  val accountTag = varchar("ACCOUNT_TAG", 4)
}



//create table SERVICE_CLIENTS (
//ALIAS varchar(255),
//PARTNERSHIP_FK int,
//ACCOUNT_TAG varchar(4),
//FOREIGN KEY (PARTNERSHIP_FK) REFERENCES PARTNERSHIPS(ID)
//) GO

object ServiceClients : Table("SERVICE_CLIENTS") {
  val alias = varchar("ALIAS", 255)
  val partnershipFk = reference("PARTNERSHIP_FK", Partnerships.id)
  val accountTag = varchar("ACCOUNT_TAG", 4)
}





//create table DEDICATED_ACCOUNTS (
//ACCOUNT_NUMBER int,
//CLIENT_ALIAS varchar(255),
//FOREIGN KEY (ACCOUNT_NUMBER) REFERENCES ACCOUNTS(NUMBER)
//) GO


object DedicatedAccounts : Table("DEDICATED_ACCOUNTS") {
  val accountNumber = reference("ACCOUNT_NUMBER", Accounts.number)
  val clientAlias = varchar("CLIENT_ALIAS", 255)
}
