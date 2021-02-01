package spotification.common

sealed trait Page[+A, +Req]
object Page {
  final case class Current[+A, +Req](content: List[A], nextReq: Req) extends Page[A, Req]
  final case class Last[+A](content: List[A]) extends Page[A, Nothing]

  def current[A, Req](content: List[A], nextReq: Req): Page[A, Req] = Current(content, nextReq)
  def last[A, Req](content: List[A]): Page[A, Req] = Last(content)
}
