package spotification.domain.spotify.authorization

import spotification.domain.HexString32

final case class Credentials(clientId: HexString32, clientSecret: HexString32)
