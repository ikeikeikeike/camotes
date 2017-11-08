package support

import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play._
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import org.scalatest.time.{ Millis, Seconds, Span }

import play.api.mvc.Result

import scala.concurrent.{ Await, Future }
import scala.concurrent.duration._

trait ModelSpecSupport extends PlaySpec with GuiceOneAppPerSuite with ScalaFutures {
  implicit val defaultPatience = PatienceConfig(timeout = Span(5, Seconds), interval = Span(500, Millis))
}
