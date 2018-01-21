package utility.using

import java.io.{ InputStream, OutputStream, Writer }

import scala.io.Source

object Closer {
  def apply[A, B](resource: A)(process: A => B)(implicit closer: Closing[A]): B =
    try process(resource) finally closer.close(resource)
}

case class Closing[-A](close: A => Unit)

object Closing {
  implicit val sourceCloser: Closing[Source] = Closing(_.close)
  implicit val writerCloser: Closing[Writer] = Closing(_.close)
  implicit val outputStreamCloser: Closing[OutputStream] = Closing(_.close)
  implicit val inputStreamCloser: Closing[InputStream] = Closing(_.close)
}

