package spotification.spotify.authorization

import spotification.common.NonBlankString

final case class AuthorizeErrorResponse(error: NonBlankString)
