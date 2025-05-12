//package io.exoquery.example.ktorm
//
//import io.exoquery.example.ktorm.Accounts
//import org.ktorm.schema.int
//import org.ktorm.schema.varchar
//
//open class AccountTypes(alias: String?) : org.ktorm.schema.Table<Nothing>("account_types") {
//  companion object : AccountTypes(null)
//  override fun aliased(alias: String) = AccountTypes(alias)
//  val accountType = varchar("account_type").primaryKey()
//  val mappingType = int("mapping_type")
//}
//
//open class Accounts(alias: String?) : org.ktorm.schema.Table<Nothing>("accounts") {
//  companion object : Accounts(null)
//  override fun aliased(alias: String) = Accounts(alias)
//  val name = varchar("name")
//  val tag = varchar("tag")
//  val number = int("number").primaryKey()
//  val type = varchar("type")
//}
////
////object OrderPermissionTypes : org.ktorm.schema.Table<Nothing>("order_permission_types") {
////  val type = varchar("type").primaryKey()
////  val description = varchar("description")
////}
//
//open class Partnerships(alias: String?) : org.ktorm.schema.Table<Nothing>("partnerships") {
//  companion object : Partnerships(null)
//  override fun aliased(alias: String) = Partnerships(alias)
//  val id = int("id").primaryKey()
//  val orderPermission = varchar("order_permission")
//  val description = varchar("description")
//}
//
////
////object Partnerships : org.ktorm.schema.Table<Nothing>("partnerships") {
////  val id = int("id").primaryKey()
////  val orderPermission = varchar("order_permission")
////  val description = varchar("description")
////}
//
//open class Registry(alias: String?) : org.ktorm.schema.Table<Nothing>("registry") {
//  companion object : Registry(null)
//  override fun aliased(alias: String) = Registry(alias)
//  val aliasCol = varchar("alias").primaryKey()
//  val recordType = varchar("record_type")
//  val market = varchar("market")
//  val description = varchar("description")
//}
//
////
////object Registry : org.ktorm.schema.Table<Nothing>("registry") {
////  val aliasCol = varchar("alias").primaryKey()
////  val recordType = varchar("record_type")
////  val market = varchar("market")
////  val description = varchar("description")
////}
//
//open class ClientAccount(alias: String?) : org.ktorm.schema.Table<Nothing>("client_account") {
//  companion object : ClientAccount(null)
//  override fun aliased(alias: String) = ClientAccount(alias)
//  val name = varchar("name")
//  val aliasCol = varchar("alias").primaryKey()
//  val officialIdentity = varchar("official_identity")
//  val orderPermission = varchar("order_permission")
//}
//
//
////
////object MerchantClients : org.ktorm.schema.Table<Nothing>("merchant_clients") {
////  val aliasCol = varchar("alias").primaryKey()
////  val code = varchar("code")
////  val orderPermission = varchar("order_permission")
////  val accountTag = varchar("account_tag")
////}
//
//open class MerchantClients(alias: String?) : org.ktorm.schema.Table<Nothing>("merchant_clients") {
//  companion object : MerchantClients(null)
//  override fun aliased(alias: String) = MerchantClients(alias)
//  val aliasCol = varchar("alias").primaryKey()
//  val code = varchar("code")
//  val orderPermission = varchar("order_permission")
//  val accountTag = varchar("account_tag")
//}
//
////
////object ServiceClients : org.ktorm.schema.Table<Nothing>("service_clients") {
////  val aliasCol = varchar("alias").primaryKey()
////  val partnershipFk = int("partnership_fk")
////  val accountTag = varchar("account_tag")
////}
//
//open class ServiceClients(alias: String?) : org.ktorm.schema.Table<Nothing>("service_clients") {
//  companion object : ServiceClients(null)
//  override fun aliased(alias: String) = ServiceClients(alias)
//  val aliasCol = varchar("alias").primaryKey()
//  val partnershipFk = int("partnership_fk")
//  val accountTag = varchar("account_tag")
//}
//
////
////object ClientAccount : org.ktorm.schema.Table<Nothing>("client_account") {
////  val name = varchar("name")
////  val aliasCol = varchar("alias")
////  val officialIdentity = varchar("official_identity")
////  val orderPermission = varchar("order_permission")
////}
//
//open class DedicatedAccounts(alias: String?) : org.ktorm.schema.Table<Nothing>("dedicated_accounts") {
//  companion object : DedicatedAccounts(null)
//  override fun aliased(alias: String) = DedicatedAccounts(alias)
//  val accountNumber = int("account_number").primaryKey()
//  val clientAlias = varchar("client_alias")
//}
//
////
////object DedicatedAccounts : org.ktorm.schema.Table<Nothing>("dedicated_accounts") {
////  val accountNumber = int("account_number").primaryKey()
////  val clientAlias = varchar("client_alias")
////}
////
