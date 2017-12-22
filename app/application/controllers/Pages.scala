package application.controllers

import javax.inject.{ Inject, Singleton }

import application.helpers
import application.views.PageView
import domain.scraper
import io.kanaka.monadic.dsl._
import play.api.data.Forms._
import play.api.data._
import play.api.http.HttpEntity
import play.api.libs.json._
import play.api.libs.ws.WSClient
import play.api.mvc._

import scala.concurrent.ExecutionContext

@Singleton
class Pages @Inject() (
  ws:     WSClient,
  helper: helpers.Pages,
  cc:     ControllerComponents,
  srv:    scraper.ScraperService
)(implicit ec: ExecutionContext) extends BaseController(cc) {
  import scraper.JsonFormatter._

  case class requestForm(src: String)
  val requestDataForm = Form(mapping(
    "src" -> text
  )(requestForm.apply)(requestForm.unapply))

  case class downloadForm(src: String, ext: String, format: String)
  val downloadDataForm = Form(mapping(
    "src" -> text,
    "ext" -> text,
    "format" -> text
  )(downloadForm.apply)(downloadForm.unapply))

  def page(none: String) = Action { implicit rs =>
    Ok(views.html.pages.page())
  }

  def info = Action.async { implicit rs =>
    for {
      fm <- requestDataForm.bindFromRequest() ?| BadRequest(views.html.pages.page())
      info <- srv.info(fm.src) ?| BadRequest(views.html.pages.page())

    } yield Ok(Json.toJson(info.entries).toString()).as(JSON)
      .withHeaders(CACHE_CONTROL -> "max-age=8640000") //      .withHeaders(CACHE_CONTROL -> "max-age=0")
  }

  def download = Action.async { implicit rs =>
    for {
      fm <- downloadDataForm.bindFromRequest() ?| BadRequest(views.html.pages.page())
      r <- srv.stream(fm.src, Seq("ext" -> fm.ext, "format" -> fm.format): _*) ?| BadRequest(views.html.pages.page())

    } yield if (r.status >= 400) BadGateway else {
      val contentType = r.headers.get("Content-Type").flatMap(_.headOption)
        .getOrElse("application/octet-stream")

      r.headers.get("Content-Length") match {
        case Some(Seq(length)) =>
          Ok.sendEntity(HttpEntity.Streamed(r.bodyAsSource, Some(length.toLong), Some(contentType)))
            .withHeaders(CACHE_CONTROL -> "max-age=8640000")
        case _ =>
          Ok.chunked(r.bodyAsSource).as(contentType)
            .withHeaders(CACHE_CONTROL -> "max-age=8640000")
      }
    }
  }
}
