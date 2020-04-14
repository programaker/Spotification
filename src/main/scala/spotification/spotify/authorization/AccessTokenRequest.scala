package spotification.spotify.authorization

import spotification.spotify.{HexString32, UriString}

final case class AccessTokenRequest(credentials: Credentials, code: HexString32, redirectUri: UriString)
