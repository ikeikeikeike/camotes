package infrastructure.scraper

import javax.inject.{Inject, Singleton}

import domain.scraper.{Entry, Root, Scrape, Scraper}
import org.json4s.DefaultFormats
import play.api.{Configuration, Logger}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ScraperImpl @Inject() (config: Configuration) extends Scraper {
  implicit val formats = DefaultFormats

  val endpoint = "http://127.0.0.1"
  lazy val http = dispatch.Http.withConfiguration(_.setReadTimeout(360000))

  def scrape(src: String)(implicit ec: ExecutionContext): Future[Either[Error, Scrape]] = {
    Logger.info(s"scrape download link by ${src}")

    for {
      json <- http(dispatch.url(src) OK dispatch.as.json4s.Json)
    } yield {
      val root = (json \ "root").camelizeKeys.extract[Root]
      val roots = if (root.entries.isEmpty) Seq(root) else root.entries

      Right(Scrape(entries = properly(roots)))
    }
  }

  def properly(roots: Seq[Root]):Seq[Entry] = roots.map(properly)
  def properly(root: Root) = {
    println(root)

    Entry(
      title    = root.title.getOrElse(""),
      content  = root.description,
      src      = root.webpageUrl.getOrElse(""),
      duration = root.duration,
      img      = root.thumbnail.getOrElse(""),
      site     = root.sitename,
      tags     = root.gatheredTags.mkString(","),
    )
  }
}
