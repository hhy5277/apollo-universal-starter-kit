package repositories

import core.slick.{SchemaInitializer, SchemaUtil}
import javax.inject.Inject
import model.{Item, ItemTable}
import model.ItemTable.ItemTable
import slick.jdbc.SQLiteProfile.api._
import slick.lifted.TableQuery

import scala.concurrent.{ExecutionContext, Future}

/**
  * Contains methods for initializing and drop a database.
  *
  * @param database base in which operations will be performed
  */
class ItemSchemaInitializer @Inject()(database: Database)
                                     (implicit executionContext: ExecutionContext) extends SchemaInitializer with SchemaUtil {
  /**
    * Entity that defines the database schema.
    */
  val items: TableQuery[ItemTable] = TableQuery[ItemTable]

  override def create(): Future[Unit] = {
    withTable(database, items, ItemTable.name, _.isEmpty) {
      DBIO.seq(items.schema.create,
      items ++= seedDatabase
      )
    }
  }

  override def drop(): Future[Unit] = {
    withTable(database, items, ItemTable.name, _.nonEmpty) {
      DBIO.seq(items.schema.drop)
    }
  }

  /**
    * Helper method for generating 'Item' entities
    *
    * @return list of 'Item' entities
    */
  def seedDatabase: List[Item] = {
    List.range(1, 100).map(num => Item(Some(num), s"Item $num"))
  }
}