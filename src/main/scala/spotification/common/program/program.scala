package spotification.common

import zio.RIO

package object program {
  type PageRIO[R, A, Req] = RIO[R, Page[A, Req]]

  def paginate[R, Req, A, B](
    z: RIO[R, B]
  )(fetchPage: Req => PageRIO[R, A, Req])(f: (List[A], RIO[R, B]) => RIO[R, B]): Req => RIO[R, B] = {
    def loop(req: Req, acc: RIO[R, B]): RIO[R, B] =
      fetchPage(req).flatMap {
        case Page(content, None)          => f(content, acc)
        case Page(content, Some(nextReq)) => f(content, loop(nextReq, acc))
      }

    loop(_, z)
  }
}
