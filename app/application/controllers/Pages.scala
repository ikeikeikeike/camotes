package application.controllers

import java.io.{ BufferedInputStream, BufferedOutputStream }
import javax.inject.{ Inject, Singleton }

import akka.stream.scaladsl.StreamConverters
import application.helpers
import domain.{ scraper => dscraper }
import io.kanaka.monadic.dsl._
import play.api.data.Forms._
import play.api.data._
import play.api.http.HttpEntity
import play.api.libs.json._
import play.api.mvc._
import utility.using.Closer

import scala.concurrent.{ ExecutionContext, Future }

@Singleton
class Pages @Inject() (
  scraper: dscraper.Scraper,
  helper:  helpers.Pages,
  cc:      ControllerComponents
)(implicit ec: ExecutionContext) extends BaseController(cc) {
  implicit val w1 = JodaWrites.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss.SSSZ") // implicit val w2 = Json.writes[HttpHeaders]
  implicit val w3 = Json.writes[dscraper.Format]
  implicit val w4 = Json.writes[dscraper.Thumbnail]
  implicit val w5 = Json.writes[dscraper.Root]
  implicit val w6 = Json.writes[dscraper.Entry]

  lazy val http = dispatch.Http.withConfiguration(_.setReadTimeout(360000))

  case class SrcForm(src: String)
  val SrcDataForm = Form(mapping("src" -> text)(SrcForm.apply)(SrcForm.unapply))

  def requestForm = Action { implicit rs =>
    Ok(views.html.pages.requestForm())
  }

  def request = Action.async { implicit rs =>
    for {
      fm <- SrcDataForm.bindFromRequest() ?| BadRequest(views.html.pages.requestForm())
      scrape <- scraper.scrape(fm.src) ?| BadRequest(views.html.pages.requestForm())
      //      entries <- helper.saveEntireEntry(scrape) ?| BadRequest(views.html.pages.requestForm())
    } yield {
      Ok(Json.toJson(scrape.entries).toString()).as(JSON).withHeaders(CACHE_CONTROL -> "max-age=8640000")
      // Redirect(routes.Pages.requestForm()).flashing("ok" -> "Downloaded!")
      // Redirect(routes.Pages.entry("12345")).flashing("ok" -> "Downloaded!")
    }
  }

  def downloadSync = Action { implicit rs =>
    val url = "https://artifacts.elastic.co/downloads/elasticsearch/elasticsearch-6.0.0.tar.gz"
    //    val url = "http://ipv4.download.thinkbroadband.com/5MB.zip"
    //    val url = "http://ipv4.download.thinkbroadband.com/512MB.zip"

    val source =
      StreamConverters.asOutputStream().mapMaterializedValue { output =>
        for (input <- http(dispatch.url(url) OK dispatch.as.Response(_.getResponseBodyAsStream))) yield {
          val in = new BufferedInputStream(input)
          Closer(new BufferedOutputStream(output)) { out => Iterator.continually(in.read).takeWhile(-1 !=).foreach(out.write) }
        }
      }

    Result(
      body = HttpEntity.Streamed(source, None, Some("video/mp4")),
      header = ResponseHeader(OK, Map(
        CACHE_CONTROL -> "max-age=0", // to be max-age=8640000
        CONTENT_DISPOSITION -> "attachment; filename=download1.mp4"
      ))
    )
  }

  def downloadAsync = Action.async { implicit rs =>
    val url = "https://artifacts.elastic.co/downloads/elasticsearch/elasticsearch-6.0.0.tar.gz"
    //    val url = "http://ipv4.download.thinkbroadband.com/5MB.zip"
    //    val url = "http://ipv4.download.thinkbroadband.com/512MB.zip"

    for {
      input <- http(dispatch.url(url) OK dispatch.as.Response(_.getResponseBodyAsStream))
    } yield {

      val source =
        StreamConverters.asOutputStream().mapMaterializedValue { output =>
          Future {
            val in = new BufferedInputStream(input)
            Closer(new BufferedOutputStream(output)) { out => Iterator.continually(in.read).takeWhile(-1 !=).foreach(out.write) }
          }
        }

      Result(
        body = HttpEntity.Streamed(source, None, Some("video/mp4")),
        header = ResponseHeader(OK, Map(
          CACHE_CONTROL -> "max-age=0", // to be max-age=8640000
          CONTENT_DISPOSITION -> "attachment; filename=download2.mp4"
        ))
      )
    }
  }
}
