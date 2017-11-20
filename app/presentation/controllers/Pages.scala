package presentation.controllers

import javax.inject.{ Inject, Singleton }

import dispatch.{ Http, as, url }
import domain.scraper.Scraper
import io.kanaka.monadic.dsl._
import play.api.data.Forms._
import play.api.data._
import play.api.mvc._
import presentation.helpers

import scala.concurrent.ExecutionContext

@Singleton
class Pages @Inject() (
  scraper: Scraper,
  helper: helpers.Pages,
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
      scrape <- scraper.scrape(fm.src) ?| warn("Failed!")
      entries <- helper.saveEntireEntry(scrape) ?| warn("Failed!")
    } yield {
      entries.map(println)
      Redirect(routes.Pages.requestForm).flashing("ok" -> "Downloaded!")
    }
  }

  private def warn(msg: String)(implicit flush: play.api.mvc.Flash) =
    BadRequest(views.html.pages.requestForm()).flashing("warn" -> msg)
}
