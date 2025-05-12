package io.exoquery.example

import io.exoquery.SqlQuery
import io.exoquery.capture
import io.exoquery.capture.invoke

val GiantQuerySql = capture{
  free(
    """
      SELECT DISTINCT
       account.name,
           alias,
       CASE WHEN code = 'EV'
         THEN cast(account.number AS VARCHAR)
         ELSE cast(account.number AS VARCHAR) || substring(alias, 1, 2) END AS OFFICIAL_IDENTITY,
       CASE WHEN order_permission IN ('A', 'S')
         THEN 'ST' ELSE 'ENH' END
      FROM  (
        (SELECT DISTINCT
        merchantClient.alias,
        merchantClient.code,
        order_permission,
        merchantClient.account_tag
        FROM MERCHANT_CLIENTS merchantClient
        JOIN REGISTRY entry ON entry.alias = merchantClient.alias
        WHERE entry.market = 'us' AND entry.record_type = 'M')
        UNION
       (SELECT DISTINCT
       serviceClient.alias,
       'EV' AS code,
       partnership.order_permission,
       serviceClient.account_tag
       FROM SERVICE_CLIENTS serviceClient
       JOIN REGISTRY entry ON entry.alias = serviceClient.alias and entry.record_type = 'S' AND entry.market = 'us'
       JOIN PARTNERSHIPS partnership ON partnership.id = serviceClient.partnership_fk)
      ) AS client
       INNER JOIN (
           ACCOUNTS account
           INNER JOIN ACCOUNT_TYPES accountType ON account.type = accountType.account_type
           LEFT JOIN DEDICATED_ACCOUNTS dedicated ON dedicated.account_number = account.number
         )
         ON   (accountType.mapping_type = 0 )
              OR  (accountType.mapping_type = 2 AND account.tag = client.account_tag)
              OR  (accountType.mapping_type = 1 AND dedicated.client_alias = client.alias)
      """
  ).asPure<SqlQuery<ClientAccount>>()
}