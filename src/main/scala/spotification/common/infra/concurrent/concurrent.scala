package spotification.common.infra

import zio.{Has, RIO, ULayer, ZIO, ZLayer}

import scala.concurrent.ExecutionContext

package object concurrent {
  type ExecutionContextEnv = Has[ExecutionContext]

  def executionContext: RIO[ExecutionContextEnv, ExecutionContext] =
    ZIO.access(_.get)

  // Let's start with the good old global ExecutionContext
  // Later we can plug something more interesting
  val ExecutionContextLayer: ULayer[ExecutionContextEnv] =
    ZLayer.succeed(ExecutionContext.global)
}
