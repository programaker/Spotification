package spotification.spotify.authorization

import spotification.{HexString32, UriString}

final case class TokenRequest(credentials: Credentials, code: HexString32, redirectUri: UriString)
