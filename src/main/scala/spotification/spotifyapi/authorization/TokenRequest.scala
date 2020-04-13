package spotification.spotifyapi.authorization

import spotification.spotifyapi.{HexString32, UriString}

final case class TokenRequest(credentials: Credentials, code: HexString32, redirectUri: UriString)
