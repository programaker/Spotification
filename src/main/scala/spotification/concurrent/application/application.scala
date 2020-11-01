package spotification.concurrent

import zio.{Has, RIO, ZIO}

import scala.concurrent.ExecutionContext

package object application {
  type ExecutionContextEnv = Has[ExecutionContext]

  def executionContext: RIO[ExecutionContextEnv, ExecutionContext] =
    ZIO.access(_.get)
}
