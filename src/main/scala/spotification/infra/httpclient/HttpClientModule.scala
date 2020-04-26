package spotification.infra.httpclient

import org.http4s.client.Client
import org.http4s.client.blaze.BlazeClientBuilder
import spotification.infra.concurrent.ExecutionContextModule.ExecutionContextService
import zio._
import zio.interop.catz._

import scala.concurrent.ExecutionContext

object HttpClientModule {
  private val makeHttpClient: URIO[ExecutionContext, RManaged[ExecutionContext, Client[Task]]] =
    ZIO.runtime[ExecutionContext].map(implicit rt => BlazeClientBuilder[Task](rt.environment).resource.toManaged)

  type HttpClientService = Has[H4sClient]

  val layer: RLayer[ExecutionContextService, HttpClientService] =
    ZLayer.fromServiceManaged(makeHttpClient.toManaged_.flatten.provide)
}
