package spotification.spotify.authorization

import spotification.NonBlankString

final case class AuthorizationResponse(code: NonBlankString, state: Option[NonBlankString])
