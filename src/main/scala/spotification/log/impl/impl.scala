package spotification.log

import io.odin.consoleLogger
import io.odin.syntax.LoggerSyntax
import spotification.log.service.LogR
import zio.{Task, TaskLayer, ZLayer, ZManaged}
import zio.interop.catz._
import zio.interop.catz.implicits._

package object impl {
  val LogLayer: TaskLayer[LogR] = ZLayer.fromManaged {
    // Why not just combine the logs and call `toManaged`?
    // This way to construct the ZManaged provides all the needed implicits through the `ceff` argument
    ZManaged.fromEffect(Task.concurrentEffect).flatMap { implicit ceff =>
      consoleLogger[Task]().withAsync().toManaged
    }
  }
}
