package spotification.infra.concurrent

import zio._

import scala.concurrent.ExecutionContext

object ExecutionContextZIO {
  type ExecutionContextService = Has[ExecutionContext]

  val executionContext: RIO[ExecutionContextService, ExecutionContext] = ZIO.access(_.get)

  // Let's start with the good old global ExecutionContext
  // Later we can plug something more interesting
  val layer: ULayer[ExecutionContextService] = ZLayer.succeed(ExecutionContext.global)
}
