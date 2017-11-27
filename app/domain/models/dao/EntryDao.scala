package domain.models.dao

import com.google.inject.ImplementedBy
import domain.models.EntryRow
import infrastructure.scalikejdbc.EntryDaoImpl
import scalikejdbc._

import scala.concurrent.{ ExecutionContext, Future }

@ImplementedBy(classOf[EntryDaoImpl])
trait EntryDao {
  def findById(id: Long)(implicit session: DBSession = AutoSession, ex: ExecutionContext): Future[Option[EntryRow]]
  def find(row: EntryRow)(implicit session: DBSession = AutoSession, ex: ExecutionContext): Future[Option[EntryRow]]
  def all(implicit session: DBSession = AutoSession, ex: ExecutionContext): Future[Seq[EntryRow]]
  def save(row: EntryRow)(implicit session: DBSession = AutoSession, ex: ExecutionContext): Future[EntryRow]
  def create(row: EntryRow)(implicit session: DBSession = AutoSession, ex: ExecutionContext): Future[EntryRow]
}
