package infrastructure.scraper

import javax.inject.Inject

import com.redis.RedisClientPool
import domain.scraper.ScraperCounter
import play.api.{ Configuration, Logger }

class ScraperCounterImpl @Inject() (config: Configuration) extends ScraperCounter {
  val key = "scraper_counter"

  lazy val logger = Logger(this.getClass)
  lazy val pool = {
    new RedisClientPool(config.get[String]("redis.host"), config.get[Int]("redis.port"),
      database = config.get[Int]("redis.scraper_counter_db"))
  }

  def increment: Option[Long] = pool.withClient(_.incr(key))
  def decrement: Option[Long] = pool.withClient(_.decr(key))
  def size: Option[String] = pool.withClient(_.get(key))
}
