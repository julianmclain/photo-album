package controllers

import models.UserRepository
import models.User
import models.api.ApiUser
import javax.inject._
import play.api.libs.json.JsError
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.ExecutionContext

@Singleton
class UserController @Inject() (
    val controllerComponents: ControllerComponents,
    val usersRepo: UserRepository,
    val loginTemplate: views.html.login
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
    Action.async(parse.json) { implicit request =>
      usersRepo.create(User(None, "hi", "email")).map { dbUser =>
        Ok(Json.toJson(dbUser))
      }
    //      val userResult = request.body.validate[ApiUser]
//      userResult.fold(
//        errors => {
//          BadRequest(Json.obj("message" -> JsError.toJson(errors)))
//        },
//        user => {
//          usersRepo.create(User(None, user.username, user.email)).map {
//            dbUser => println(dbUser)
//          }
//          Created(Json.obj("message" -> "user created"))
//        }
//      )
    }
}
