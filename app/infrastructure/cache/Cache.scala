package infrastructure.cache

import javax.inject.{ Inject, Singleton }
import scalacache.serialization.{ Codec, JavaSerializationCodec }
import scala.concurrent.{ ExecutionContext, Future }
import scala.concurrent.duration.Duration
import scalacache._
import com.google.inject.ImplementedBy

@ImplementedBy(classOf[GuavaScalaCacheImpl])
trait Cache {
  def caching[F](keyParts: Any*)(ttl: Duration)(f: => Future[F])(implicit ec: ExecutionContext): Future[F]
  def removeAll(): Future[Unit]
}

@Singleton
class GuavaScalaCacheImpl @Inject() extends Cache {
  import guava._
  implicit val scalaCache = ScalaCache(GuavaCache())
  def caching[F](keyParts: Any*)(ttl: Duration)(f: => Future[F])(implicit ec: ExecutionContext): Future[F] =
    if (ttl.toMillis == 0) f else scalacache.cachingWithTTL(keyParts)(ttl)(f)

  def removeAll(): Future[Unit] = scalacache.removeAll()
}
