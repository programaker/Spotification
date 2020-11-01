package spotification.concurrent

import spotification.concurrent.application.ExecutionContextEnv
import zio._

import scala.concurrent.ExecutionContext

package object infra {
  // Let's start with the good old global ExecutionContext
  // Later we can plug something more interesting
  val ExecutionContextLayer: ULayer[ExecutionContextEnv] = ZLayer.succeed(ExecutionContext.global)
}
