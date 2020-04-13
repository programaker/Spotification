package spotification.spotify.authorization

import spotification.spotify.{HexString32, UriString}

final case class TokenRequest(credentials: Credentials, code: HexString32, redirectUri: UriString)
