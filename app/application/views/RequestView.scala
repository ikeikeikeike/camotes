package application.views
import domain.models.EntryRow

case class RequestView(
  entries: Seq[EntryRow]
) extends View {}