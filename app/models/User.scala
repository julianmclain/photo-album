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
final case class User(id: Long, username: String, email: String)

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

    def * = (id, username, email) <> ((User.apply _).tupled, User.unapply)
  }

  /**
    * The starting point for all queries on the users table.
    */
  private val users = TableQuery[UsersTable]

  def findById(id: Long): Future[User] =
    db.run {
      users.filter(_.id === id).result.head
    }

  def create(username: String, email: String): Future[User] =
    db.run {
      (users.map(p => (p.username, p.email))
        returning users.map(_.id)
        into ((usernameEmail, id) =>
          User(id, usernameEmail._1, usernameEmail._2)
        )) += (username, email)
    }

  def list: Future[Seq[User]] =
    db.run {
      users.result
    }
}
