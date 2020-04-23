package spotification.spotify.authorization

import org.http4s.dsl.Http4sDsl
import zio.Task

package object presentation {

  type H4sDsl = Http4sDsl[Task]

}
