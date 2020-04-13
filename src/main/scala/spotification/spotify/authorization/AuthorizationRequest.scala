package spotification.spotify.authorization

import spotification.NonBlankString

final case class AuthorizationRequest(credentials: Credentials, scopes: List[Scope], state: Option[NonBlankString])
