package spotification.presentation

sealed abstract class GenericResponse extends Product with Serializable
object GenericResponse {
  final case class Success(success: String) extends GenericResponse
  final case class Error(error: String) extends GenericResponse
}
