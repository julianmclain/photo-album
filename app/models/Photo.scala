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
final case class Photo(id: Long, title: String, user: String)

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
    def user = column[String]("user")

    def * = (id, title, user).mapTo[Photo]
  }

  /**
    * The starting point for all queries on the photo table.
    */
  private val photos = TableQuery[PhotoTable]

  def create(title: String, user: String): Future[Photo] =
    db.run {
      // We create a projection of just the title and user columns, since we're not inserting a value for the id column
      (photos.map(p => (p.title, p.user))
      // Now define it to return the id, because we want to know what id was generated for the person
        returning photos.map(_.id)
      // And we define a transformation for the returned value, which combines our original parameters with the
      // returned id
        into ((titleUser, id) => Photo(id, titleUser._1, titleUser._2))
      // And finally, insert the person into the database
      ) += (title, user)
    }

  def list: Future[Seq[Photo]] =
    db.run {
      photos.result
    }
}
