package controllers

import javax.inject.Inject
import javax.inject.Singleton
import play.api.mvc.AnyContent
import play.api.mvc.BaseController
import play.api.mvc.ControllerComponents
import play.api.mvc.Request

import scala.concurrent.ExecutionContext

@Singleton
class IndexController @Inject() (
    val controllerComponents: ControllerComponents
)(implicit ec: ExecutionContext)
    extends BaseController {

  def index =
    Action { implicit request: Request[AnyContent] =>
      Redirect(routes.AlbumController.viewAlbums())
    }
}
