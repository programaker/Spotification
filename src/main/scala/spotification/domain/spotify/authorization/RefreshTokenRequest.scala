package spotification.domain.spotify.authorization

import eu.timepit.refined.auto._
import spotification.domain.GrantType

final case class RefreshTokenRequest(
  client_id: ClientId,
  client_secret: ClientSecret,
  grant_type: GrantType, //TODO fixed value, can be removed
  refresh_token: RefreshToken
)

object RefreshTokenRequest {
  def of(
    client_id: ClientId,
    client_secret: ClientSecret,
    refresh_token: RefreshToken
  ): RefreshTokenRequest =
    RefreshTokenRequest(
      client_id = client_id,
      client_secret = client_secret,
      grant_type = "refresh_token",
      refresh_token = refresh_token
    )
}
