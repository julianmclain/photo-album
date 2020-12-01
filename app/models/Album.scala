package models

import javax.inject.Inject
import javax.inject.Singleton
import slick.jdbc.JdbcProfile
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

final case class Album(id: Option[Long], title: String, description: String)

@Singleton
final class AlbumRepository @Inject() (
    dbConfigProvider: DatabaseConfigProvider
)(implicit
    ec: ExecutionContext
) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  private class AlbumTable(tag: Tag) extends Table[Album](tag, "albums") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def title = column[String]("title")
    def description = column[String]("description")

    def * = (id.?, title, description).mapTo[Album]
  }

  private val albums = TableQuery[AlbumTable]

  def create(title: String, description: String): Future[Album] =
    db.run {
      (albums.map(a => (a.title, a.description))
        returning albums.map(_.id)
        into ((titleDescription, id) =>
          Album(Some(id), titleDescription._1, titleDescription._2)
        )) += (title, description)
    }

  def list: Future[Seq[Album]] =
    db.run {
      albums.result
    }
}
