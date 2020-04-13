package spotification.spotifyapi.authorization

import spotification.spotifyapi.{HexString32, UriString}

final case class Credentials(clientId: HexString32, clientSecret: HexString32, redirectUri: UriString)
