package spotification.core

/** Generic `ErrorObject` used by most Spotify endpoints when something goes wrong */
final case class ErrorResponse(status: Int, message: String)
