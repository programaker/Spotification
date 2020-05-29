package spotification.presentation

object GenericResponse {
  final case class Success(success: String)
  final case class Error(error: String)
}
