package spotification.common

import zio.RIO

package object program {
  type PageRIO[R, A, Req] = RIO[R, Page[A, Req]]

  def paginate[R, Req, A, B](
    start: RIO[R, B]
  )(fetchPage: Req => PageRIO[R, A, Req])(f: (RIO[R, B], List[A]) => RIO[R, B]): Req => RIO[R, B] = {
    def loop(req: Req, acc: RIO[R, B]): RIO[R, B] =
      fetchPage(req).flatMap {
        case Page(content, None)          => f(acc, content)
        case Page(content, Some(nextReq)) => loop(nextReq, f(acc, content))
      }

    loop(_, start)
  }
}
