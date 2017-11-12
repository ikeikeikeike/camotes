package presentation.controllers

import scala.concurrent.ExecutionContext
import javax.inject.{ Inject, Singleton }

import dispatch.{ Http, as, url }
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import io.kanaka.monadic.dsl._
import domain.models.dao.EntryDao

@Singleton
class Pages @Inject() (
  dao: EntryDao,
  cc: ControllerComponents)(implicit ec: ExecutionContext) extends BaseController(cc) {

  case class SrcForm(src: String)
  val SrcDataForm = Form(mapping("src" -> text)(SrcForm.apply)(SrcForm.unapply))

  // http http://localhost:8000/api/info/`echo -n 'https://www.youtube.com/watch?v=FW1nd4e53EM' | base64`
  def proxy = Action.async { implicit rs =>
    val svc = url("http://www.example.com")
    val future = Http.default(svc OK as.String)

    for (f <- future) yield Ok(f).as(HTML)
  }

  def info = Action.async { implicit rs =>
    val svc = url("http://www.example.com")
    val future = Http.default(svc OK as.String)

    for (f <- future) yield Ok(f).as(HTML)
  }

  def requestForm = Action { implicit rs =>
    Ok(views.html.pages.requestForm())
  }

  def request = Action.async { implicit rs =>
    for {
      fm <- SrcDataForm.bindFromRequest() ?| warn("Failed!")
      fHttp <- Http.default(url(fm.src) OK as.String) ?| warn("Failed!")
      entry <- dao.create(fm.src, "") ?| warn("Failed!")
    } yield Redirect(routes.Pages.requestForm).flashing("ok" -> "Downloaded!")
  }

  private def warn(msg: String)(implicit flush: play.api.mvc.Flash) =
    BadRequest(views.html.pages.requestForm()).flashing("warn" -> msg)
}
