package spotification

import eu.timepit.refined.auto._
import spotification.config.ConcurrentConfig
import spotification.config.service.ConcurrentConfigEnv
import spotification.effect.accessRIO
import zio.{Has, RIO, URLayer, ZLayer}

import java.util.concurrent.Executors
import scala.concurrent.ExecutionContext

package object concurrent {
  type ExecutionContextEnv = Has[ExecutionContext]

  val ExecutionContextLayer: URLayer[ConcurrentConfigEnv, ExecutionContextEnv] =
    ZLayer.fromService { config =>
      ExecutionContext.fromExecutor(Executors.newFixedThreadPool(config.numberOfThreads))
    }

  def executionContext: RIO[ExecutionContextEnv, ExecutionContext] = accessRIO
}
