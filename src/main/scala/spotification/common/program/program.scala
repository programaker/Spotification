package spotification.common

import zio.{RIO, Task}

package object program {
  type PageRIO[R, A, Req] = RIO[R, Page[A, Req]]

  def paginate_[R, Req, A, B](
    z: RIO[R, B]
  )(
    fetchPage: Req => PageRIO[R, A, Req]
  )(
    f: List[A] => RIO[R, B]
  )(
    combine: (RIO[R, B], RIO[R, B]) => RIO[R, B]
  ): Req => RIO[R, B] = {
    def loop(req: Req): RIO[R, B] =
      fetchPage(req).flatMap { case Page(content, maybeNextReq) =>
        val thisPageResult = f(content)

        val nextPageResult = maybeNextReq match {
          case Some(nextReq) => loop(nextReq)
          case None          => z
        }

        combine(thisPageResult, nextPageResult)
      }

    loop
  }

  def paginate[R, Req, A, B](
    start: RIO[R, B]
  )(
    fetchPage: Req => PageRIO[R, A, Req]
  )(
    f: (RIO[R, B], List[A]) => RIO[R, B]
  ): Req => RIO[R, B] = {
    def loop(req: Req, acc: RIO[R, B]): RIO[R, B] =
      fetchPage(req).flatMap {
        case Page(content, None) =>
          println("||| Last page")
          f(acc, content)
        case Page(content, Some(nextReq)) =>
          println(">=> Page")
          loop(nextReq, f(acc, content))
      }

    loop(_, start)
  }
}
