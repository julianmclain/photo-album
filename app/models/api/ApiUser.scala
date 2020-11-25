package models.api

import play.api.libs.json.Json

case class ApiUser(username: String, email: String)

object ApiUser {
  implicit val format = Json.format[ApiUser]
}
