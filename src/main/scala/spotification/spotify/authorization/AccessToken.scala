package spotification.spotify.authorization

import spotification.NonBlankString

final case class AccessToken(value: NonBlankString) extends AnyVal
