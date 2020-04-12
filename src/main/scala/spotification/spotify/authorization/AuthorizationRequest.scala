package spotification.spotify.authorization

import spotification.NonBlankString

final case class AuthorizationRequest(scopes: List[Scope], state: Option[NonBlankString])
