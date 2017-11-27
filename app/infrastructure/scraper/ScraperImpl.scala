package infrastructure.scraper

import javax.inject.{ Inject, Singleton }

import domain.scraper.{ Entry, Root, Scrape, Scraper }
import org.apache.commons.codec.binary.Base64
import org.json4s.DefaultFormats
import play.api.{ Configuration, Logger }

import scala.concurrent.{ ExecutionContext, Future }

@Singleton
class ScraperImpl @Inject() (config: Configuration) extends Scraper {
  implicit val dFormats = DefaultFormats

  val endpoint = "http://localhost:8000/api/info"
  lazy val http = dispatch.Http.withConfiguration(_.setReadTimeout(120000))

  def scrape(src: String)(implicit ec: ExecutionContext): Future[Either[Error, Scrape]] = {
    val uri = s"$endpoint/${new String(Base64.encodeBase64(src.getBytes))}"
    Logger.info(s"scrape download link by ${src} to be encoded $uri")

    for {
      json <- http(dispatch.url(uri) OK dispatch.as.json4s.Json)
    } yield {
      val root = (json \ "root").camelizeKeys.extract[Root]
      val roots = if (root.entries.isEmpty) Seq(root) else root.entries

      Right(Scrape(entries = properly(roots)))
    }
  }

  def properly(roots: Seq[Root]): Seq[Entry] = roots.map(properly)
  def properly(root: Root) = {
    Entry(
      title = root.title.getOrElse(""),
      content = root.description,
      formats = root.gatheredFormats,
      src = root.webpageUrl.getOrElse(""),
      likeCount = root.likeCount,
      viewCount = root.viewCount,
      duration = root.duration,
      img = root.thumbnail.getOrElse(""),
      site = root.sitename,
      tags = root.gatheredTags.mkString(","))
  }
}
