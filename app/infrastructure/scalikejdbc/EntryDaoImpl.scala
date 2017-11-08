package infrastructure.scalikejdbc

import javax.inject.{ Inject, Singleton }

import scala.concurrent.{ ExecutionContext, Future }
import scala.async.Async.async
import scalikejdbc._
import domain.models.dao.{ EntryDao }
import domain.models.EntryRow

@Singleton
class EntryDaoImpl @Inject() extends EntryDao {
  private def *(rs: WrappedResultSet): EntryRow = EntryRow(
    id = rs.long("id"),
    url = rs.string("url"))

  def save(url: String)(implicit session: DBSession = AutoSession, ex: ExecutionContext): Future[Long] = {
    async {
      sql"""insert into entries(url) values(${url})
      """.updateAndReturnGeneratedKey.apply()
    }
  }

  def find(id: Long)(implicit session: DBSession = AutoSession, ex: ExecutionContext): Future[Option[EntryRow]] = {
    async {
      sql"""select * from entries where id = ${id}""".map(*).single.apply()
    }
  }
}
