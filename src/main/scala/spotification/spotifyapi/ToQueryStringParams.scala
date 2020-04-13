package spotification.spotifyapi

trait ToQueryStringParams[T] {
  def convert(t: T): Map[String, String]
}

object ToQueryStringParams {
  def apply[T: ToQueryStringParams]: ToQueryStringParams[T] = implicitly[ToQueryStringParams[T]]
}
