package domain.models
import org.joda.time.{ DateTime, DateTimeZone }

case class EntryRow(
  id: Long = 0,
  title: String = "",
  content: Option[String] = None,
  src: String = "",
  dest: String = "",
  img: String = "",
  alt: Option[String] = None,
  site: String = "",
  tags: String = "",
  createdAt: DateTime = DateTime.now(DateTimeZone.UTC),
  updatedAt: DateTime = DateTime.now(DateTimeZone.UTC)
)
