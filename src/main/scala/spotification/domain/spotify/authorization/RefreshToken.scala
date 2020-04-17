package spotification.domain.spotify.authorization

import spotification.domain.NonBlankString

final case class RefreshToken(value: NonBlankString)
