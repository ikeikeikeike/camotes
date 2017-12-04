package infrastructure.scraper

import javax.inject.{ Inject, Singleton }

import domain.scraper._
import org.apache.commons.codec.binary.Base64
import play.api.libs.json.JsSuccess
import play.api.libs.ws.{ WSClient, WSResponse }
import play.api.{ Configuration, Logger }

import scala.concurrent.duration.{ Duration, HOURS }
import scala.concurrent.{ ExecutionContext, Future }

@Singleton
class ScraperImpl @Inject() (config: Configuration, ws: WSClient) extends Scraper {

  import JsonFormatter._

  val infoEndpoint = "http://localhost:8000/api/info"
  val streamEndpoint = "http://localhost:8000/api/stream"
  val timeout = Duration(5, HOURS)

  def info(src: String)(implicit ec: ExecutionContext): Future[Either[Error, Scrape]] = { // TODO: Left
    for (response <- ws.url(toUrl(infoEndpoint, src)).get()) yield (response.json \ "root").validate[Root] match {
      case s: JsSuccess[Root] =>
        val root = s.get
        val roots = root.entries.getOrElse(Seq(root))

        Right(Scrape(entries = properly(roots)))
    }
  }

  def stream(src: String)(implicit ec: ExecutionContext): Future[Either[Error, WSResponse]] = {
    val client = ws.url(toUrl(streamEndpoint, src)).withRequestTimeout(timeout).withMethod("GET")

    for (r <- client.stream()) yield Right(r)
  }

  private def toUrl(endpoint: String, src: String): String = {
    val url = s"$endpoint/${new String(Base64.encodeBase64(src.getBytes))}"
    Logger.info(s"${src} to be encoded $url")
    url
  }

  private def properly(roots: Seq[Root]): Seq[Entry] = roots.map(properly)
  private def properly(root: Root) = Entry(
    title = root.title.getOrElse(""),
    content = root.description,
    formats = root.gatheredFormats,
    src = root.webpageUrl.getOrElse(""),
    likeCount = root.likeCount,
    viewCount = root.viewCount,
    duration = root.duration,
    img = root.thumbnail.getOrElse(""),
    site = root.sitename,
    tags = root.gatheredTags.mkString(",")
  )

}
