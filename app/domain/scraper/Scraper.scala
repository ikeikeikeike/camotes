package domain.scraper

import com.google.inject.ImplementedBy
import com.netaporter.uri.Uri
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._
import play.api.libs.ws.WSResponse

import scala.concurrent.{ ExecutionContext, Future }

object JsonFormatter {
  implicit def option[T: Format]: Format[Option[T]] = new Format[Option[T]] {
    override def reads(json: JsValue): JsResult[Option[T]] = json.validateOpt[T]
    override def writes(o: Option[T]): JsValue = o match {
      case Some(t) => implicitly[Writes[T]].writes(t)
      case None    => JsNull
    }
  }

  implicit val datetimeWrites = JodaWrites.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss.SSSZ")

  implicit val videoFormatF: Format[VideoFormat] = (
    (__ \ "manifest_url").formatNullable[String] and
    (__ \ "ext").formatNullable[String] and
    (__ \ "url").formatNullable[String] and
    (__ \ "protocol").formatNullable[String] and
    (__ \ "format").formatNullable[String] and
    (__ \ "format_id").formatNullable[String] and
    (__ \ "tbr").formatNullable[Float]
  )(VideoFormat.apply, unlift(VideoFormat.unapply))

  implicit val httpHeadersF: Format[HttpHeaders] = (
    (__ \ "accept").formatNullable[String] and
    (__ \ "acceptCharset").formatNullable[String] and
    (__ \ "acceptEncoding").formatNullable[String] and
    (__ \ "acceptLanguage").formatNullable[String] and
    (__ \ "userAgent").formatNullable[String]
  )(HttpHeaders.apply, unlift(HttpHeaders.unapply))

  implicit val thumbnailF: Format[Thumbnail] = (
    (__ \ "id").format[String] and
    (__ \ "url").format[String]
  )(Thumbnail.apply, unlift(Thumbnail.unapply))

  implicit val entryF: Format[Entry] = (
    (__ \ "title").format[String] and
    (__ \ "content").formatNullable[String] and
    (__ \ "src").format[String] and
    (__ \ "duration").formatNullable[Int] and
    (__ \ "like_count").formatNullable[Int] and
    (__ \ "view_count").formatNullable[Int] and
    (__ \ "img").format[String] and
    (__ \ "site").format[String] and
    (__ \ "tags").format[String] and
    (__ \ "formats").format[Seq[VideoFormat]]
  )(Entry.apply, unlift(Entry.unapply))

  implicit val rootF: Format[Root] = (
    (__ \ "id").formatNullable[String] and
    (__ \ "title").formatNullable[String] and
    (__ \ "webpage_url").formatNullable[String] and
    (__ \ "description").formatNullable[String] and
    (__ \ "manifest_url").formatNullable[String] and
    (__ \ "ext").formatNullable[String] and
    (__ \ "url").formatNullable[String] and
    (__ \ "protocol").formatNullable[String] and
    (__ \ "format").formatNullable[String] and
    (__ \ "formatId").formatNullable[String] and
    (__ \ "tbr").formatNullable[Float] and
    (__ \ "extractor").formatNullable[String] and
    (__ \ "thumbnail").formatNullable[String] and
    (__ \ "duration").formatNullable[Float] and
    (__ \ "like_count").formatNullable[Int] and
    (__ \ "view_count").formatNullable[Int] and
    (__ \ "tags").formatNullable[Seq[String]] and
    (__ \ "categories").formatNullable[Seq[String]] and
    (__ \ "thumbnails").formatNullable[Seq[Thumbnail]] and
    (__ \ "formats").formatNullable[Seq[VideoFormat]] and
    (__ \ "requested_formats").formatNullable[Seq[VideoFormat]] and
    (__ \ "entries").formatNullable[Seq[Root]]
  )(Root.apply, unlift(Root.unapply))

  implicit val lifecycleF: Format[Lifecycle] = (
    (__ \ "name").format[String] and
    (__ \ "host").format[String] and
    (__ \ "info").format[String] and
    (__ \ "stream").format[String]
  )(Lifecycle.apply, unlift(Lifecycle.unapply))

}

object Formatter {
  def resolution(format: Option[String]): Option[String] = {
    Some(format.map(f => f.split('-')).getOrElse(Array.empty)
      .toSeq.last.trim.split(' ').toSeq.head)
  }
}

case class Scrape(entries: Seq[Entry])

case class Entry(
  title:     String,
  content:   Option[String]   = None,
  src:       String,
  duration:  Option[Int],
  likeCount: Option[Int],
  viewCount: Option[Int],
  img:       String,
  site:      String,
  tags:      String,
  formats:   Seq[VideoFormat]
)

case class Thumbnail(id: String, url: String)

case class HttpHeaders(
  accept:         Option[String],
  acceptCharset:  Option[String],
  acceptEncoding: Option[String],
  acceptLanguage: Option[String],
  userAgent:      Option[String]
)

case class VideoFormat(
  manifestUrl: Option[String],
  ext:         Option[String],
  url:         Option[String],
  protocol:    Option[String],
  format:      Option[String],
  formatId:    Option[String],
  tbr:         Option[Float]
)

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
  duration:         Option[Float],
  likeCount:        Option[Int],
  viewCount:        Option[Int],
  tags:             Option[Seq[String]],
  categories:       Option[Seq[String]],
  thumbnails:       Option[Seq[Thumbnail]],
  formats:          Option[Seq[VideoFormat]],
  requestedFormats: Option[Seq[VideoFormat]],
  entries:          Option[Seq[Root]]
) {
  val rightRequestedFormats = requestedFormats.getOrElse(Seq.empty)
  val rightFormats = formats.getOrElse(Seq.empty)
  val rightTags = tags.getOrElse(Seq.empty)
  val rightCategories = categories.getOrElse(Seq.empty)

  def gatheredTags: Seq[String] = (rightTags ++ rightCategories).distinct

  def gatheredFormats: Seq[VideoFormat] = {
    val list = rightFormats ++ rightRequestedFormats

    if (list.nonEmpty) list else {
      val videoFormat =
        VideoFormat.apply(
          manifestUrl = manifestUrl,
          ext = ext,
          url = url,
          protocol = protocol,
          format = format,
          formatId = formatId,
          tbr = tbr
        )

      Seq(videoFormat)
    }
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

case class Lifecycle(
  host:   String,
  info:   String,
  name:   String,
  stream: String
)

@ImplementedBy(classOf[infrastructure.scraper.ScraperImpl])
trait Scraper {
  def info(src: String)(implicit ec: ExecutionContext): Future[Either[Error, Scrape]]
  def stream(src: String, params: (String, String)*)(implicit ec: ExecutionContext): Future[Either[Error, WSResponse]]
}
