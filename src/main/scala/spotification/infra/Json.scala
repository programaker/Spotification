package spotification.infra

import cats.Applicative
import io.circe.{Decoder, Encoder}
import io.estatico.newtype.Coercible
import io.estatico.newtype.ops._
import org.http4s.EntityEncoder
import org.http4s.circe.jsonEncoderOf

object Json {
  object Implicits {
    implicit def coercibleEncoder[A, B](implicit c: Coercible[A, B], e: Encoder[B]): Encoder[A] =
      e.contramap(_.coerce[B])

    implicit def coercibleDecoder[A, B](implicit c: Coercible[B, A], d: Decoder[B]): Decoder[A] =
      d.map(_.coerce[A])

    implicit def apJsonEntityEncoder[F[_]: Applicative, A: Encoder]: EntityEncoder[F, A] =
      jsonEncoderOf[F, A]
  }
}
