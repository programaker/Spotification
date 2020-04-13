package spotification.spotify.authorization

import spotification.spotify.NonBlankString

final case class AuthorizationResponse(code: NonBlankString, state: Option[NonBlankString])
