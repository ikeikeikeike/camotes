package domain.scraper

import javax.inject.{ Inject, Singleton }

import play.api.Configuration
import play.api.libs.ws.WSResponse

import scala.concurrent.{ ExecutionContext, Future }

@Singleton
class ScraperService @Inject() (config: Configuration, scraper: Scraper)(implicit ec: ExecutionContext) {
  def info(src: String): Future[Either[Error, Scrape]] = scraper.info(src)
  def stream(src: String, params: (String, String)*): Future[Either[Error, WSResponse]] = scraper.stream(src)
}
