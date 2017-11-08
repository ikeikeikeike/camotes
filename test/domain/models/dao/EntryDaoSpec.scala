package domain.models.dao

import scala.concurrent._
import scala.concurrent.duration.Duration
import support.ModelSpecSupport

class EntryDaoSpec extends ModelSpecSupport {

  import scala.concurrent.ExecutionContext.Implicits.global

  "EntryDao" should {
    lazy val dao = app.injector.instanceOf(classOf[EntryDao])

    "#request" should {
      "request and find it" in {
        val id = Await.result(dao.save(url = "https://example.com"), Duration.Inf)
        whenReady(dao.find(id)) { iO =>
          val i = iO.get
          i.id mustEqual id
        }
      }
    }
  }
}
