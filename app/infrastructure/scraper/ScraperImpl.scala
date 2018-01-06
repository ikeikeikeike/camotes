package infrastructure.scraper

import javax.inject.Inject

import domain.scraper._
import org.apache.commons.codec.binary.Base64
import play.api.libs.json.{JsError, JsSuccess}
import play.api.libs.ws.{WSClient, WSResponse}
import play.api.{Configuration, Logger}

import scala.concurrent.duration.{Duration, HOURS}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random

class ScraperImpl @Inject() (config: Configuration, ws: WSClient) extends Scraper {
  lazy val logger = Logger(this.getClass)

  lazy val endpoint = config.get[String]("scraper.endpoint")
  val timeout = Duration(5, HOURS)

  import JsonFormatter._

  def info(src: String)(implicit ec: ExecutionContext): Future[Either[Error, Scrape]] = {
    for {
      l <- lifecycle()
      r <- ws.url(toUrl(l.info, src)).get()
    } yield (r.json \ "root").validate[Root] match {
      case s: JsError =>
        logger.error(s.toString)
        Left(new Error(s.toString))

      case s: JsSuccess[Root] =>
        val root = s.get
        val roots = root.entries.getOrElse(Seq(root))

        Right(Scrape(entries = properly(roots)))
    }
  }

  def stream(src: String, params: (String, String)*)(implicit ec: ExecutionContext): Future[Either[Error, WSResponse]] = {
    for {
      l <- lifecycle()
      r <- ws.url(toUrl(l.stream, src)).withQueryStringParameters(params: _*).withRequestTimeout(timeout).stream()
    } yield Right(r)
  }

  private def properly(roots: Seq[Root]): Seq[Entry] = roots.map(properly)
  private def properly(root: Root) = Entry(
    title = root.title.getOrElse(""),
    content = root.description,
    formats = root.gatheredFormats,
    src = root.webpageUrl.getOrElse(""),
    likeCount = root.likeCount,
    viewCount = root.viewCount,
    duration = root.duration.map(_.toInt),
    img = root.thumbnail.getOrElse(""),
    site = root.sitename,
    tags = root.gatheredTags.mkString(",")
  )

  private def toUrl(base: String, src: String): String = {
    val url = s"$base${new String(Base64.encodeBase64(src.getBytes))}"
    logger.info(s"$src to be encoded $url")
    url
  }

  private def lifecycle()(implicit ec: ExecutionContext): Future[Lifecycle] = {
    logger.info(s"lifecycle: ${endpoint}")
    for (r <- ws.url(endpoint).get()) yield {
      val lifecycles = (r.json \ "root").as[Seq[Lifecycle]]
      Random.shuffle(lifecycles).head
    }
  }

}
