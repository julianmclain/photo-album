package controllers

import javax.inject._
import models.Album
import models.User
import play.api.mvc._

import scala.concurrent.ExecutionContext

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class AlbumController @Inject() (
    val controllerComponents: ControllerComponents,
    val albumsTemplate: views.html.albums
)(implicit ec: ExecutionContext)
    extends BaseController {

  /**
    * Create an Action to render an HTML page.
    *
    * The configuration in the `routes` file means that this method
    * will be called when the application receives a `GET` request with
    * a path of `/`.
    */
  def albumsGet =
    Action { implicit request: Request[AnyContent] =>
      Ok(
        albumsTemplate(
          User(Some(1), "userme", "hi@aol.com"),
          List(Album(None, "title of album", "cool description"))
        )
      )
    }
}
