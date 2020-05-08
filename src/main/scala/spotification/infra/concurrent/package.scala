package spotification.infra

import zio.{Has, RIO, ULayer, ZIO, ZLayer}

import scala.concurrent.ExecutionContext

package object concurrent {
  type ExecutionContextModule = Has[ExecutionContext]
  object ExecutionContextModule {
    val executionContext: RIO[ExecutionContextModule, ExecutionContext] = ZIO.access(_.get)

    // Let's start with the good old global ExecutionContext
    // Later we can plug something more interesting
    val layer: ULayer[ExecutionContextModule] = ZLayer.succeed(ExecutionContext.global)
  }
}
