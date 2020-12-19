package spotification.authorization.json

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.refined.{refinedDecoder, refinedEncoder}
import io.circe.{Decoder, Encoder}
import io.estatico.newtype.ops.toCoercibleIdOps
import spotification.authorization._
import spotification.common.NonBlankString

object implicits {
  implicit val AccessTokenDecoder: Decoder[AccessToken] =
    implicitly[Decoder[NonBlankString]].map(_.coerce[AccessToken])

  implicit val AccessTokenEncoder: Encoder[AccessToken] =
    implicitly[Encoder[NonBlankString]].contramap(_.coerce[NonBlankString])

  implicit val RefreshTokenDecoder: Decoder[RefreshToken] =
    implicitly[Decoder[NonBlankString]].map(_.coerce[RefreshToken])

  implicit val RefreshTokenEncoder: Encoder[RefreshToken] =
    implicitly[Encoder[NonBlankString]].contramap(_.coerce[NonBlankString])

  implicit val AuthorizeErrorResponseEncoder: Encoder[AuthorizeErrorResponse] = deriveEncoder
  implicit val RefreshTokenResponseDecoder: Decoder[RefreshTokenResponse] = deriveDecoder
  implicit val AccessTokenResponseDecoder: Decoder[AccessTokenResponse] = deriveDecoder
  implicit val AccessTokenResponseEncoder: Encoder[AccessTokenResponse] = deriveEncoder
}
