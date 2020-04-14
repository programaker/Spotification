package spotification.spotify.authorization

import spotification.spotify.{HexString32, UriString}

final case class Credentials(clientId: HexString32, clientSecret: HexString32, redirectUri: UriString)
