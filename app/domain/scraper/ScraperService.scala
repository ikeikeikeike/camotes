package domain.scraper

import javax.inject.Inject

import play.api.Configuration
import play.api.libs.ws.WSResponse

import scala.concurrent.{ ExecutionContext, Future }

class ScraperService @Inject() (
  config:  Configuration,
  scraper: Scraper,
  counter: ScraperCounter
)(implicit ec: ExecutionContext) {
  def info(src: String): Future[Either[Error, Scrape]] =
    scraper.info(src)

  def stream(src: String, params: (String, String)*): Future[Either[Error, WSResponse]] =
    scraper.stream(src, params: _*)

  def increment: Option[Long] = counter.increment
}
