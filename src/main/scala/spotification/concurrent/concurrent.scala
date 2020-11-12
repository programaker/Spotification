package spotification

import java.util.concurrent.Executors

import scala.concurrent.ExecutionContext

import spotification.effect.accessRIO
import zio.Has
import zio.RIO
import zio.ZLayer
import zio.TaskLayer
import spotification.config.ConcurrentConfig
import spotification.config.source.ConcurrentConfigLayer
import eu.timepit.refined.auto._

package object concurrent {
  type ExecutionContextEnv = Has[ExecutionContext]

  val ExecutionContextLayer: TaskLayer[ExecutionContextEnv] = {
    val l1 = ZLayer.fromService[ConcurrentConfig, ExecutionContext] { config => 
      ExecutionContext.fromExecutor(Executors.newFixedThreadPool(config.numberOfThreads))
    }

    ConcurrentConfigLayer >>> l1
  }

  def executionContext: RIO[ExecutionContextEnv, ExecutionContext] = accessRIO
}
