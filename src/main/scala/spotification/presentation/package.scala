package spotification

import org.http4s.dsl.Http4sDsl
import zio.Task

package object presentation {

  type H4sDsl = Http4sDsl[Task]

}
