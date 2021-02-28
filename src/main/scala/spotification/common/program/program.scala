package spotification.common

import zio.RIO

package object program {
  type PageRIO[R, A, Req] = RIO[R, Page[A, Req]]

  def paginate[R, Req, A, B](
    z: RIO[R, B]
  )(
    fetchPage: Req => PageRIO[R, A, Req]
  )(
    consumePage: List[A] => RIO[R, B]
  )(
    combinePages: (RIO[R, B], RIO[R, B]) => RIO[R, B]
  ): Req => RIO[R, B] = {
    def loop(req: Req): RIO[R, B] =
      fetchPage(req).flatMap { case Page(content, maybeNextReq) =>
        val thisPageResult = consumePage(content)

        val nextPageResult = maybeNextReq match {
          case Some(nextReq) => loop(nextReq)
          case None          => z
        }

        combinePages(thisPageResult, nextPageResult)
      }

    loop
  }
}
