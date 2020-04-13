package spotification.spotify.authorization

import spotification.{HexString32, UriString}

final case class TokenRequest(code: HexString32, redirectUri: UriString)
