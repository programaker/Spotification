package spotification.spotify.authorization

import spotification.spotify.HexString32

final case class Credentials(clientId: HexString32, clientSecret: HexString32)
