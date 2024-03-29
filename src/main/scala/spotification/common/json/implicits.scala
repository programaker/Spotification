package spotification.common.json

import cats.effect.Concurrent
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}
import org.http4s.circe.{jsonEncoderOf, jsonOf}
import org.http4s.{EntityDecoder, EntityEncoder}
import spotification.common.{ErrorResponse, GenericResponse}

object implicits {
  implicit def entityEncoderF[F[_], A: Encoder]: EntityEncoder[F, A] = jsonEncoderOf
  implicit def entityDecoderF[F[_]: Concurrent, A: Decoder]: EntityDecoder[F, A] = jsonOf

  implicit val ErrorResponseDecoder: Decoder[ErrorResponse] = deriveDecoder
  implicit val GenericResponseSuccessEncoder: Encoder[GenericResponse.Success] = deriveEncoder
  implicit val GenericResponseErrorEncoder: Encoder[GenericResponse.Error] = deriveEncoder
}
