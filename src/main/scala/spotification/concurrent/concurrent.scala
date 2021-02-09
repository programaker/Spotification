package spotification

import eu.timepit.refined.auto._
import spotification.config.service.ConcurrentConfigR
import spotification.effect.accessRIO
import zio.{Has, RIO, URLayer, ZLayer}

import java.util.concurrent.Executors
import scala.concurrent.ExecutionContext

package object concurrent {
  type ExecutionContextR = Has[ExecutionContext]
  def executionContext: RIO[ExecutionContextR, ExecutionContext] = accessRIO

  val ExecutionContextLayer: URLayer[ConcurrentConfigR, ExecutionContextR] =
    ZLayer.fromService { config =>
      ExecutionContext.fromExecutor(Executors.newFixedThreadPool(config.numberOfThreads))
    }
}
