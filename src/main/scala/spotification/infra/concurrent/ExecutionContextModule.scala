package spotification.infra.concurrent

import zio.{Has, ULayer, ZLayer}

import scala.concurrent.ExecutionContext

object ExecutionContextModule {
  type ExecutionContextService = Has[ExecutionContext]

  // Let's start with the good old global ExecutionContext
  // Later we can plug something more interesting
  val layer: ULayer[ExecutionContextService] = ZLayer.succeed(ExecutionContext.global)
}
