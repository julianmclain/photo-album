package models

import javax.inject.Inject
import javax.inject.Singleton
import slick.jdbc.JdbcProfile
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

final case class AlbumMember(albumId: Long, userId: Long)

@Singleton
final class AlbumMemberRepository @Inject() (
    dbConfigProvider: DatabaseConfigProvider
)(implicit
    ec: ExecutionContext
) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  private class AlbumMemberTable(tag: Tag)
      extends Table[AlbumMember](tag, "album_users") {
    def id = primaryKey("id", (albumId, userId))
    def albumId = column[Long]("album_id")
    def userId = column[Long]("user_id")

    def * = (albumId, userId).mapTo[AlbumMember]
  }

  private val albumMembers = TableQuery[AlbumMemberTable]

  def list: Future[Seq[AlbumMember]] =
    db.run {
      albumMembers.result
    }
}
