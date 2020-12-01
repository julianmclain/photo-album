package models

import javax.inject.Inject
import javax.inject.Singleton
import slick.jdbc.JdbcProfile
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

/**
  * Case class representing a row in the photos table
  */
final case class Photo(
    id: Option[Long],
    title: String,
    caption: String,
    url: String,
    userId: Long
)

/**
  * A repository for photos
  */
@Singleton
final class PhotoRepository @Inject() (
    dbConfigProvider: DatabaseConfigProvider
)(implicit
    ec: ExecutionContext
) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  /**
    * Schema for the photos table
    */
  private class PhotoTable(tag: Tag) extends Table[Photo](tag, "photos") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def title = column[String]("title")
    def caption = column[String]("caption")
    def url = column[String]("url")
    def userId = column[Long]("user")

    def * = (id.?, title, caption, url, userId).mapTo[Photo]
  }

  /**
    * The starting point for all queries on the photo table.
    */
  private val photos = TableQuery[PhotoTable]

//  def create(title: String, user: String): Future[Photo] =
//    db.run {
//      // We create a projection of the non-id columns, since we're not inserting a value for the id column
//      (photos.map(p => (p.title, p.caption, p.url, p.userId))
//      // define the method to return the record id, because we want to know what id was generated
//        returning photos.map(_.id)
//      // And we define a transformation for the returned value, which combines our original parameters with the
//      // returned id
//        into ((nonIdFields, id) => nonIdFields.copy(x = ))
//      // And finally, insert the person into the database
//      ) += (title, user)
//    }

  def list: Future[Seq[Photo]] =
    db.run {
      photos.result
    }
}
