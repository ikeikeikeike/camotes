package domain.scraper

import com.google.inject.ImplementedBy
import com.netaporter.uri.Uri

import scala.concurrent.{ ExecutionContext, Future }

@ImplementedBy(classOf[infrastructure.scraper.ScraperImpl])
trait Scraper {
  def scrape(src: String)(implicit ec: ExecutionContext): Future[Either[Error, Scrape]]
}

object Formatter {
  def resolution(format: Option[String]) = {
    Some(format.map(f => f.split('-')).getOrElse(Array.empty)
      .toSeq.last.trim.split(' ').toSeq.head)
  }
}

case class Scrape(entries: Seq[Entry])
case class Entry(
  title:     String,
  content:   Option[String] = None,
  src:       String,
  duration:  Option[Int],
  likeCount: Option[Int],
  viewCount: Option[Int],
  img:       String,
  site:      String,
  tags:      String,
  formats:   Seq[Format]
)

case class Thumbnail(id: String, url: String)
//case class HttpHeaders(
//  accept: String,
//  acceptCharset: String,
//  acceptEncoding: String,
//  acceptLanguage: String,
//  userAgent: String)

case class Format(
  manifestUrl: Option[String],
  ext:         Option[String],
  url:         Option[String],
  protocol:    Option[String],
  format:      Option[String],
  formatId:    Option[String],
  tbr:         Option[Float],
  resolution:  Option[String]
//  httpHeaders: Option[HttpHeaders]
) {
  def this(
    manifestUrl: Option[String],
    ext:         Option[String],
    url:         Option[String],
    protocol:    Option[String],
    format:      Option[String],
    formatId:    Option[String],
    tbr:         Option[Float]
  ) = {
    this(manifestUrl, ext, url, protocol, format, formatId, tbr, Formatter.resolution(format))
  }
}

case class Root(
  // httpHeaders:  Option[HttpHeaders],
  // extractorkey: Option[String],
  id:               Option[String],
  title:            Option[String],
  webpageUrl:       Option[String],
  description:      Option[String],
  manifestUrl:      Option[String], // format
  ext:              Option[String], // format
  url:              Option[String], // format
  protocol:         Option[String], // format
  format:           Option[String], // format
  formatId:         Option[String], // format
  tbr:              Option[Float], // format
  extractor:        Option[String],
  thumbnail:        Option[String],
  duration:         Option[Int],
  likeCount:        Option[Int],
  viewCount:        Option[Int],
  tags:             Seq[String],
  categories:       Seq[String],
  thumbnails:       Seq[Thumbnail],
  formats:          Seq[Format],
  requestedFormats: Seq[Format],
  entries:          Seq[Root]
) {

  def gatheredTags: Seq[String] = (tags ++ categories).distinct

  def gatheredFormats: Seq[Format] = {
    val list = formats ++ requestedFormats

    if (list.nonEmpty) list else
      Seq(Format(manifestUrl, ext, url, protocol, format, formatId, tbr, Formatter.resolution(format)))
  }

  def sitename: String = {
    (extractor, webpageUrl) match {
      case (Some("generic"), Some(pageUrl)) =>
        val uri = Uri.parse(pageUrl)
        val (host, suffix) = (uri.host, uri.publicSuffix)

        suffix.flatMap(f => host.map(_.replace(s".${f}", "")))
          .map(f => f.split('.').last).getOrElse("")

      case (Some(text), _) =>
        text.toLowerCase

      case _ =>
        ""
    }
  }
}
