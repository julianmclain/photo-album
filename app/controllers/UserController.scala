package controllers

import models.UserRepository
import models.api.ApiUser
import javax.inject._
import play.api.libs.json.JsError
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.ExecutionContext

@Singleton
class UserController @Inject() (
    val controllerComponents: ControllerComponents,
    val usersRepo: UserRepository
)(implicit ec: ExecutionContext)
    extends BaseController {

  def getUser(id: Long) =
    Action.async { implicit request =>
      for {
        user <- usersRepo.findById(id)
      } yield Ok(Json.toJson(user))
    }

  def getUsers =
    Action.async { implicit request =>
      for {
        users <- usersRepo.list
      } yield Ok(Json.toJson(users))
    }

  def createUser =
    Action(parse.json) { implicit request =>
      val userResult = request.body.validate[ApiUser]
      userResult.fold(
        errors => {
          BadRequest(Json.obj("message" -> JsError.toJson(errors)))
        },
        user => {
          usersRepo.create(user.username, user.email)
          Created(Json.obj("message" -> "user created"))
        }
      )
    }
}
