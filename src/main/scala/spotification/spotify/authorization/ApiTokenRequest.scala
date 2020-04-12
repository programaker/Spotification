package spotification.spotify.authorization

import spotification.{HexString32, UriString}

final case class ApiTokenRequest(code: HexString32, grantType: GrantType, redirectUri: UriString)
