package spotification.common.presentation

import io.circe.Encoder
import org.http4s.EntityEncoder
import org.http4s.circe.jsonEncoderOf
import zio.ZIO

trait JsonSupport[R, E] {
  type F[A] = ZIO[R, E, A]

  implicit def zioJsonEntityEncoder[A](implicit encoder: Encoder[A]): EntityEncoder[F, A] =
    jsonEncoderOf[F, A]
}
