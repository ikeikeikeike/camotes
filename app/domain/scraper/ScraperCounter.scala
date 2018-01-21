package domain.scraper

import com.google.inject.ImplementedBy

@ImplementedBy(classOf[infrastructure.scraper.ScraperCounterImpl])
trait ScraperCounter {
  def increment: Option[Long]
  def decrement: Option[Long]
  def size: Option[String]
}
