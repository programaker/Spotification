package spotification.common

final case class Page[A, Req](content: List[A], nextReq: Option[Req])
