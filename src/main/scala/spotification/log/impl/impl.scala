package spotification.log

import io.odin.consoleLogger
import io.odin.syntax.LoggerSyntax
import spotification.effect.{managedTaskDispatcher, managedZIORuntime}
import spotification.log.service.LogR
import zio.blocking.Blocking
import zio.clock.Clock
import zio.interop.catz.{asyncRuntimeInstance, catsIOResourceSyntax}
import zio.{RLayer, Task, ZLayer}

package object impl {
  val LogLayer: RLayer[Clock with Blocking, LogR] = ZLayer.fromManaged {
    managedZIORuntime[Clock with Blocking].flatMap { implicit rt =>
      managedTaskDispatcher.flatMap { implicit dispatcher =>
        consoleLogger[Task]().withAsync().toManaged
      }
    }
  }
}
