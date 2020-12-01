package models

import javax.inject.Inject
import javax.inject.Singleton
import slick.jdbc.JdbcProfile
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.Json

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

/**
  * Case class representing a row in the users table
  */
final case class User(id: Option[Long], username: String, email: String)

object User {
  implicit val format = Json.format[User]
}

/**
  * A repository for users
  */
@Singleton
final class UserRepository @Inject() (
    dbConfigProvider: DatabaseConfigProvider
)(implicit
    ec: ExecutionContext
) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  /**
    * Schema for the users table
    */
  private class UsersTable(tag: Tag) extends Table[User](tag, "users") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def username = column[String]("username")
    def email = column[String]("email")

    // If you have a companion object to your case class you can't use the `mapTo[myCaseClass]` macro
    // https://queirozf.com/entries/slick-error-message-value-tupled-is-not-a-member-of-object
    def * = (id.?, username, email) <> ((User.apply _).tupled, User.unapply)
  }

  /**
    * The starting point for all queries on the users table.
    */
  private val users = TableQuery[UsersTable]

  def findById(id: Long): Future[User] =
    db.run {
      users.filter(_.id === id).result.head
    }

  def create(user: User): Future[User] = {
    // Inspired by https://stackoverflow.com/a/58081439/14444354 and iterable code
    val insertQuery =
      users returning users.map(_.id) into ((_, id) => user.copy(id = Some(id)))
    val action = insertQuery += user
    db.run(action)
  }

  def list: Future[Seq[User]] =
    db.run {
      users.result
    }
}
