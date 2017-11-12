package domain.models.dao

import scala.concurrent.{ ExecutionContext, Future }
import scalikejdbc._
import com.google.inject.ImplementedBy

import domain.models.EntryRow
import infrastructure.scalikejdbc.{ EntryDaoImpl }

@ImplementedBy(classOf[EntryDaoImpl])
trait EntryDao {
  def create(src: String, title: String)(implicit session: DBSession = AutoSession, ex: ExecutionContext): Future[EntryRow]
  def find(id: Long)(implicit session: DBSession = AutoSession, ex: ExecutionContext): Future[Option[EntryRow]]
}
