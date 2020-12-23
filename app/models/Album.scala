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

  def findById(id: Long): Future[Option[Album]] =
    db.run(albums.filter(_.id === id).result.headOption)

  def create(album: Album): Future[Album] = {
    val insertQuery = albums returning albums.map(_.id) into ((_, id) =>
      album.copy(id = Some(id))
    )
    val action = insertQuery += album
    db.run(action)
  }

  def update(album: Album): Future[Int] = {
    val query = for {
      a <- albums if a.id === album.id
    } yield a

    db.run(query.update(album))
  }

  def deleteById(id: Long): Future[Int] = {
    val query = for {
      a <- albums if a.id === id
    } yield a

    db.run(query.delete)
  }

  def list: Future[Seq[Album]] =
    db.run {
      albums.result
    }
}
