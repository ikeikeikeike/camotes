package domain.models.dao

import scala.concurrent.{ ExecutionContext, Future }
import scalikejdbc._
import com.google.inject.ImplementedBy
import domain.models.EntryRow
import infrastructure.scalikejdbc.{ EntryDaoImpl }

@ImplementedBy(classOf[EntryDaoImpl])
trait EntryDao {
  def save(url: String)(implicit session: DBSession = AutoSession, ex: ExecutionContext): Future[Long]
  def find(id: Long)(implicit session: DBSession = AutoSession, ex: ExecutionContext): Future[Option[EntryRow]]
}
