package tasks

import javax.inject.Inject

import akka.actor.ActorSystem
import domain.scraper.ScraperCounter
import play.api.libs.json._
import play.api.libs.ws.WSClient
import play.api.{ Configuration, Logger }

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

class SlackNotify @Inject() (
  actorSystem: ActorSystem,
  config:      Configuration,
  counter:     ScraperCounter,
  ws:          WSClient
)(implicit executionContext: ExecutionContext) {
  lazy val logger = Logger(this.getClass)
  lazy val endpoint = config.get[String]("slack.endpoint")

  actorSystem.scheduler.schedule(initialDelay = 10.seconds, interval = 1.day) {
    if (!endpoint.isEmpty) {
      ws.url(endpoint).post(Json.obj(
        "link_names" -> 1,
        "channel" -> "#alert_camotes",
        "username" -> "Summaries",
        "text" -> s"Download Size: ${counter.size.getOrElse(0)}"
      ))
    }
  }
}