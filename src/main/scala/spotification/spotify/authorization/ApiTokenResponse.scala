package spotification.spotify.authorization

import spotification.NonBlankString

final case class ApiTokenResponse(accessToken: NonBlankString, refreshToken: NonBlankString)
