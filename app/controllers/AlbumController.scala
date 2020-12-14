package controllers

import javax.inject.Singleton
import javax.inject.Inject
import models.Album
import models.AlbumRepository
import models.User
import play.api.data.Form
import play.api.data.Forms.mapping
import play.api.data.Forms.nonEmptyText
import play.api.mvc._
import play.api.i18n.I18nSupport
import play.api.Logging

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

case class AlbumData(title: String, description: String)

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class AlbumController @Inject() (
    val albumsTemplate: views.html.albums,
    val albumTemplate: views.html.album,
    val newAlbumTemplate: views.html.newAlbum,
    val notFoundTemplate: views.html.notFound,
    val controllerComponents: ControllerComponents,
    val albumsRepo: AlbumRepository
)(implicit ec: ExecutionContext)
    extends BaseController
    with I18nSupport
    with Logging {

  private val albumForm = Form(
    mapping(
      "title" -> nonEmptyText,
      "description" -> nonEmptyText
    )(AlbumData.apply)(AlbumData.unapply)
  )

  /**
    * Typically, controller functions return an Action[T]. Actions are
    * functions that map a `Request` to a `Result`.
    *
    * The configuration in the `routes` file means that this method
    * will be called when the application receives a `GET` request with
    * a path of `/albums`.
    */
  def viewAlbums =
    Action.async { implicit request: Request[AnyContent] =>
      for {
        albums <- albumsRepo.list
      } yield Ok(
        albumsTemplate(
          User(Some(1), "userme", "hi@aol.com"),
          albums
        )
      )
    }

  def viewAlbum(id: Long): Action[AnyContent] =
    Action.async { implicit request: Request[AnyContent] =>
      albumsRepo.findById(id).map {
        case Some(album) => Ok(albumTemplate(album))
        case None        => NotFound(notFoundTemplate())
      }
    }

  def createAlbum: Action[AnyContent] =
    Action { implicit request: Request[AnyContent] =>
      Ok(newAlbumTemplate(albumForm))
    }

  def editAlbum(id: Long): Action[AnyContent] =
    Action { implicit request =>
      Redirect(routes.AlbumController.viewAlbums())
    }

  def deleteAlbum(id: Long): Action[AnyContent] =
    Action { implicit request =>
      Redirect(routes.AlbumController.viewAlbums())
    }

  def saveAlbum: Action[AnyContent] =
    Action.async { implicit request =>
      albumForm.bindFromRequest.fold(
        formWithErrors => {
          Future.successful(BadRequest(newAlbumTemplate(formWithErrors)))
        },
        albumData => {
          albumsRepo
            .create(
              Album(None, albumData.title, albumData.description)
            )
            .map(_ => Redirect(routes.AlbumController.viewAlbums()))
        }
      )
    }
}
