package spotification.infra

import cats.Applicative
import io.circe.{Decoder, Encoder}
import io.estatico.newtype.Coercible
import io.estatico.newtype.ops._
import org.http4s.EntityEncoder
import org.http4s.circe.jsonEncoderOf

object Json {
  implicit def coercibleEncoder[A, B](implicit c: Coercible[A, B], e: Encoder[B]): Encoder[A] =
    Encoder[B].contramap(_.coerce[B])

  implicit def coercibleDecoder[A: Coercible[B, *], B: Decoder]: Decoder[A] =
    Decoder[B].map(_.coerce[A])

  implicit def apJsonEntityEncoder[F[_]: Applicative, A: Encoder]: EntityEncoder[F, A] =
    jsonEncoderOf[F, A]
}
