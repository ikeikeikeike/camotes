package presentation.controllers

import scala.concurrent.{ ExecutionContext }
import javax.inject.{ Inject, Singleton }

import dispatch.{ Http, url, as }
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import io.kanaka.monadic.dsl._
import domain.models.dao.EntryDao

@Singleton
class Admins @Inject() (
  dao: EntryDao,
  cc: ControllerComponents)(implicit ec: ExecutionContext) extends BaseController(cc) {

  case class URLForm(url: String)
  val URLDataForm = Form(mapping("url" -> text)(URLForm.apply)(URLForm.unapply))

  def proxy = Action.async { implicit rs =>
    val svc = url("http://www.example.com")
    val future = Http.default(svc OK as.String)

    for (f <- future) yield Ok(f).as(HTML)
  }

  def requestForm = Action { implicit rs =>
    Ok(views.html.admin.requestForm())
  }

  def request = Action.async { implicit rs =>
    for {
      fm <- URLDataForm.bindFromRequest() ?| BadRequest(views.html.admin.requestForm())
      _ <- dao.save(url = fm.url) ?| BadRequest(views.html.admin.requestForm())
    } yield {
      Redirect(routes.Admins.requestForm).flashing("message" -> "Downloaded!")
    }
  }
}
