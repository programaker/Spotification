package spotification.common

final case class Page[+A, +Req](content: List[A], nextReq: Option[Req])
object Page {
  def current[A, Req](content: List[A], nextReq: Req): Page[A, Req] = Page(content, Some(nextReq))
  def last[A, Req](content: List[A]): Page[A, Req] = Page(content, None)
}
