package controllers

import javax.inject._
import models.UserRepository
import play.api.mvc._

import scala.concurrent.Await
import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt

@Singleton
class AuthController @Inject() (
    val controllerComponents: ControllerComponents,
    val usersRepo: UserRepository
)(implicit ec: ExecutionContext)
    extends BaseController {

  def getLogin: Action[AnyContent] =
    Action { implicit request: Request[AnyContent] =>
      val user = Await.result(usersRepo.findById(1), 5.seconds)
      Ok(views.html.login(user.username))
    }

  def login: Action[AnyContent] =
    Action { implicit request: Request[AnyContent] =>
      Ok(views.html.login("j"))
    }
}
