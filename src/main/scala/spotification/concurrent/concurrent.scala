package spotification

import zio.{Has, RIO, ULayer, ZIO, ZLayer}

import scala.concurrent.ExecutionContext
import java.util.concurrent.Executors

package object concurrent {
  type ExecutionContextEnv = Has[ExecutionContext]

  // TODO => Add config for the number of threads
  val ExecutionContextLayer: ULayer[ExecutionContextEnv] =
    ZLayer.succeed(ExecutionContext.fromExecutor(Executors.newFixedThreadPool(2)))

  def executionContext: RIO[ExecutionContextEnv, ExecutionContext] =
    ZIO.access(_.get)
}
