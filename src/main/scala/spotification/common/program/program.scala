package spotification.common

import spotification.authorization.program.AuthorizationProgramsR
import spotification.playlist.program.PlaylistProgramsR
import spotification.track.program.TrackProgramsR
import zio.RIO

package object program {
  type AllProgramsR = AuthorizationProgramsR with PlaylistProgramsR with TrackProgramsR
  type PageRIO[R, A, Req] = RIO[R, Page[A, Req]]

  def paginate[R, Req, A, B](
    fetch: Req => PageRIO[R, A, Req]
  )(
    z: RIO[R, B]
  )(
    f: (RIO[R, B], List[A]) => RIO[R, B]
  ): Req => RIO[R, B] = {
    def loop(req: Req, acc: RIO[R, B]): RIO[R, B] =
      fetch(req).flatMap {
        case Page(content, None)          => f(acc, content)
        case Page(content, Some(nextReq)) => loop(nextReq, f(acc, content))
      }

    loop(_, z)
  }
}
