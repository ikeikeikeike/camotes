package domain.scraper

import com.google.inject.ImplementedBy

import scala.concurrent.{ ExecutionContext, Future }

@ImplementedBy(classOf[infrastructure.scraper.ScraperImpl])
trait Scraper {
  def scrape(src: String)(implicit ec: ExecutionContext): Future[Either[Error, Scrape]]
}

case class Scrape(entries: Seq[Entry])
case class Entry(
  title: String,
  content: Option[String] = None,
  src: String,
  duration: Option[Int],
  img: String,
  alt: Option[String] = None,
  site: String,
  tags: String,
)

case class Thumbnail(id: String, url: String)
case class HttpHeaders(
  accept: String,
  acceptCharset: String,
  acceptEncoding: String,
  acceptLanguage: String,
  userAgent: String)

case class Format(
  manifestUrl: Option[String],
  ext: Option[String],
  url: Option[String],
  protocol: Option[String],
  format: Option[String],
  formatId: Option[String],
  tbr: Option[Float],
  httpHeaders: Option[HttpHeaders])

case class Root(
  id: Option[String],
  webpageUrl: Option[String],
  manifestUrl: Option[String],
  title: Option[String],
  ext: Option[String],
  url: Option[String],
  protocol: Option[String],
  format: Option[String],
  formatId: Option[String],
  extractor: Option[String],
  extractorKey: Option[String],
  description: Option[String],
  thumbnail: Option[String],
  duration: Option[Int],
  tbr: Option[Float],
  tags: Seq[String],
  categories: Seq[String],
  httpHeaders: Option[HttpHeaders],
  thumbnails: Seq[Thumbnail],
  formats: Seq[Format],
  requestedFormats: Seq[Format],
  entries: Seq[Root]
) {

  def gatheredTags:Seq[String] = (tags ++ categories).distinct
  def sitename:String = {
    ""
  }
}
