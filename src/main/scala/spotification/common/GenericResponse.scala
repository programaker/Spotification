package spotification.common

sealed trait GenericResponse
object GenericResponse {
  final case class Success(success: String) extends GenericResponse
  final case class Error(error: String) extends GenericResponse
}
