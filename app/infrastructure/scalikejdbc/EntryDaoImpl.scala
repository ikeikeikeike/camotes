package infrastructure.scalikejdbc

import javax.inject.Inject

import domain.models.EntryRow
import domain.models.dao.EntryDao
import org.joda.time.{ DateTime, DateTimeZone }
import scalikejdbc._

import scala.async.Async.async
import scala.concurrent.{ ExecutionContext, Future }

class EntryDaoImpl @Inject() extends EntryDao {
  private def *(rs: WrappedResultSet): EntryRow = EntryRow(
    id = rs.long("id"),
    title = rs.string("title"), // Page Title
    content = rs.stringOpt("content"), // Page Content
    src = rs.string("src"), // Original URL
    dest = rs.string("dest"), // Download URL
    duration = rs.intOpt("duration"),
    img = rs.string("img"), // Image url
    site = rs.string("site"), // TODO: will be one to many
    tags = rs.string("tags"), // TODO: will be many to many
    createdAt = rs.jodaDateTime("created_at"),
    updatedAt = rs.jodaDateTime("updated_at")
  )

  def find(row: EntryRow)(implicit session: DBSession = AutoSession, ex: ExecutionContext): Future[Option[EntryRow]] = {
    val sql =
      if (row.id > 0)
        sql"""select * from entries where id = ${row.id}"""
      else
        sql"""select * from entries where src = ${row.src}"""

    async(sql.map(*).single.apply())
  }

  def findById(id: Long)(implicit session: DBSession = AutoSession, ex: ExecutionContext): Future[Option[EntryRow]] = {
    async(sql"""select * from entries where id = ${id}""".map(*).single.apply())
  }

  def all(implicit session: DBSession = AutoSession, ex: ExecutionContext): Future[Seq[EntryRow]] = {
    async(sql"""select * from entries""".map(*).list.apply())
  }

  def create(row: EntryRow)(implicit session: DBSession = AutoSession, ex: ExecutionContext): Future[EntryRow] = {
    val dt = DateTime.now(DateTimeZone.UTC)
    async {
      sql"""insert into entries(title, src, dest, duration, img, site, tags, created_at, updated_at)
      values(${row.title}, ${row.src}, '', ${row.duration}, ${row.img}, ${row.site}, ${row.tags}, ${dt}, ${dt})"""
        .updateAndReturnGeneratedKey.apply()
    }.map(id => row.copy(
      id = id, createdAt = dt, updatedAt = dt
    ))
  }

  def save(row: EntryRow)(implicit session: DBSession = AutoSession, ex: ExecutionContext): Future[EntryRow] = {
    find(row).flatMap {
      case Some(r) => Future.successful(r)
      case None    => create(row)
    }
  }
}
