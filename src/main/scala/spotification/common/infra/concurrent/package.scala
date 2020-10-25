package spotification.common.infra

import zio.{Has, RIO, ULayer, ZIO, ZLayer}

import scala.concurrent.ExecutionContext

package object concurrent {
  type ExecutionContextModule = Has[ExecutionContext]
  object ExecutionContextModule {
    // Let's start with the good old global ExecutionContext
    // Later we can plug something more interesting
    val live: ULayer[ExecutionContextModule] = ZLayer.succeed(ExecutionContext.global)
  }

  val executionContext: RIO[ExecutionContextModule, ExecutionContext] =
    ZIO.access(_.get)
}
