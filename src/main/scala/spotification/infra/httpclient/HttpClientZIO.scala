package spotification.infra.httpclient

import org.http4s.client.Client
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.client.middleware.Logger
import spotification.infra.concurrent.ExecutionContextZIO.ExecutionContextService
import zio._
import zio.interop.catz._

import scala.concurrent.ExecutionContext

object HttpClientZIO {
  type HttpClientService = Has[H4sClient]

  val layer: RLayer[ExecutionContextService, HttpClientService] = {
    val makeHttpClient: URIO[ExecutionContext, RManaged[ExecutionContext, Client[Task]]] =
      ZIO.runtime[ExecutionContext].map(implicit rt => BlazeClientBuilder[Task](rt.environment).resource.toManaged)

    val addLogger: Client[Task] => Client[Task] =
      Logger(logHeaders = true, logBody = true)(_)

    ZLayer.fromServiceManaged(makeHttpClient.toManaged_.flatten.map(addLogger).provide)
  }
}
