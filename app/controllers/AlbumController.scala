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
import play.api.data.Forms.longNumber
import play.api.data.Forms.optional

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

case class AlbumData(id: Option[Long], title: String, description: String)

@Singleton
class AlbumController @Inject() (
    val albumsTemplate: views.html.albums,
    val albumTemplate: views.html.album,
    val newAlbumTemplate: views.html.newAlbum,
    val editAlbumTemplate: views.html.editAlbum,
    val notFoundTemplate: views.html.notFound,
    val controllerComponents: ControllerComponents,
    val albumsRepo: AlbumRepository
)(implicit ec: ExecutionContext)
    extends BaseController
    with I18nSupport
    with Logging {

  private val albumForm = Form(
    mapping(
      "id" -> optional(longNumber),
      "title" -> nonEmptyText,
      "description" -> nonEmptyText
    )(AlbumData.apply)(AlbumData.unapply)
  )

  def viewAlbums: Action[AnyContent] =
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
    Action.async { implicit request =>
      albumsRepo.findById(id).map {
        case Some(album) => {
          val albumData = AlbumData(album.id, album.title, album.description)
          Ok(editAlbumTemplate(albumForm.fill(albumData)))
        }
        case None => NotFound
      }
    }

  def deleteAlbum(id: Long): Action[AnyContent] =
    Action.async { implicit request =>
      albumsRepo.deleteById(id) map {
        case 0 => NotFound(notFoundTemplate())
        case 1 => Redirect(routes.AlbumController.viewAlbums())
        case n @ _ => {
          logger.error(s"deleting album id: $id modified $n records")
          InternalServerError
        }
      }

    }

  def saveAlbum: Action[AnyContent] =
    Action.async { implicit request =>
      albumForm.bindFromRequest.fold(
        formWithErrors => {
          Future.successful(BadRequest(newAlbumTemplate(formWithErrors)))
        },
        {
          case AlbumData(Some(id), title, description) => {
            albumsRepo.update(Album(Some(id), title, description)).map {
              case 0 => NotFound(notFoundTemplate())
              case 1 => Redirect(routes.AlbumController.viewAlbum(id))
              case n @ _ => {
                logger.error(s"updating album id: $id modified $n records")
                InternalServerError
              }
            }
          }
          case AlbumData(None, title, description) =>
            albumsRepo
              .create(Album(None, title, description))
              .map(album =>
                Redirect(routes.AlbumController.viewAlbum(album.id.get))
              )
        }
      )
    }
}
