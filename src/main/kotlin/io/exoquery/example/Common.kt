package io.exoquery.example

import io.exoquery.controller.jdbc.JdbcController
import io.exoquery.controller.jdbc.JdbcControllers
import io.exoquery.controller.runActions
import io.zonky.test.db.postgres.embedded.EmbeddedPostgres
import javax.sql.DataSource

object Common {


  val postgres = EmbeddedPostgres.start()
  val ds: DataSource = postgres.postgresDatabase

  val postgresController = JdbcControllers.Postgres(ds)
  suspend fun setupDB(ctx: JdbcController) {
    ctx.runActions(
      """
      CREATE TABLE Person (
        id SERIAL PRIMARY KEY,
        name VARCHAR(255),
        age INT
      );
      INSERT INTO Person (name, age) VALUES
        ('Leib', 19),
        ('Leah', 17),
        ('Marina', 27),
        ('Karina', 37);
      CREATE TABLE Address (
        ownerId INT,
        street VARCHAR(255),
        zip VARCHAR(255)
      );
      INSERT INTO Address (ownerId, street, zip) VALUES
        (1, '123 St.', '12345'),
        (2, '456 St.', '67890'),
        (3, '789 St.', '54321');
    """
    )
  }
}
