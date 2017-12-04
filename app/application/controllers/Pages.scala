package application.controllers

import javax.inject.{ Inject, Singleton }

import application.helpers
import domain.{ scraper => dscraper }
import io.kanaka.monadic.dsl._
import play.api.data.Forms._
import play.api.data._
import play.api.http.HttpEntity
import play.api.libs.json._
import play.api.libs.ws.WSClient
import play.api.mvc._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.{ Duration, HOURS }

@Singleton
class Pages @Inject() (
  scraper: dscraper.Scraper,
  ws:      WSClient,
  helper:  helpers.Pages,
  cc:      ControllerComponents
)(implicit ec: ExecutionContext) extends BaseController(cc) {
  import dscraper.JsonFormatter._

  case class SrcForm(src: String)
  val SrcDataForm = Form(mapping("src" -> text)(SrcForm.apply)(SrcForm.unapply))

  def requestForm = Action { implicit rs =>
    Ok(views.html.pages.requestForm())
  }

  def request = Action.async { implicit rs =>
    for {
      fm <- SrcDataForm.bindFromRequest() ?| BadRequest(views.html.pages.requestForm())
      info <- scraper.info(fm.src) ?| BadRequest(views.html.pages.requestForm())

    } yield Ok(Json.toJson(info.entries).toString()).as(JSON).withHeaders(CACHE_CONTROL -> "max-age=8640000")
  }

  def download = Action.async { implicit rs =>
    //    val src = "http://ipv4.download.thinkbroadband.com/512MB.zip"

    for {
      fm <- SrcDataForm.bindFromRequest() ?| BadRequest(views.html.pages.requestForm())
      r <- scraper.stream(fm.src) ?| BadRequest(views.html.pages.requestForm())
    } yield {
      if (r.status < 400) BadGateway else {
        val contentType = r.headers.get("Content-Type").flatMap(_.headOption)
          .getOrElse("application/octet-stream")

        r.headers.get("Content-Length") match {
          case Some(Seq(length)) =>
            Ok.sendEntity(HttpEntity.Streamed(r.bodyAsSource, Some(length.toLong), Some(contentType)))
          case _ =>
            Ok.chunked(r.bodyAsSource).as(contentType)
        }
      }
    }
  }
}
