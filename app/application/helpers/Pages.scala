package application.helpers

//import domain.models.EntryRow
//import domain.scraper.{ Entry, Scrape }

//import scala.concurrent.Future

class Pages //@Inject() (dao: EntryDao)
{
  //  import scala.concurrent.ExecutionContext.Implicits.global
  //
  //  def saveEntireEntry(scrape: Scrape): Future[Seq[EntryRow]] =
  //    Future.sequence(scrape.entries.map(entry => saveEntry(entry)))
  //
  //  def saveEntireEntry(entries: Seq[Entry]): Future[Seq[EntryRow]] =
  //    Future.sequence(entries.map(entry => saveEntry(entry)))
  //
  //  def saveEntireEntry(entry: Entry): Future[Seq[EntryRow]] =
  //    Future.sequence(Seq(saveEntry(entry)))
  //
  //  def saveEntry(entry: Entry): Future[EntryRow] = {
  //    dao.save(EntryRow(
  //      title = entry.title,
  //      content = entry.content,
  //      src = entry.src,
  //      img = entry.img,
  //      site = entry.site,
  //      tags = entry.tags,
  //      duration = entry.duration
  //    ))
  //  }
}
