package spotification.infra.httpclient

import org.http4s.client.Client
import org.http4s.client.blaze.BlazeClientBuilder
import zio._
import zio.interop.catz._

import scala.concurrent.ExecutionContext

object HttpClientModule {
  type HttpClientService = Has[H4sClient]

  val layer: ZLayer[ExecutionContext, Throwable, HttpClientService] =
    ZLayer.fromManaged(makeHttpClient.toManaged_.flatten)

  private def makeHttpClient: URIO[ExecutionContext, RManaged[ExecutionContext, Client[Task]]] =
    ZIO.runtime[ExecutionContext].map(implicit rt => BlazeClientBuilder[Task](rt.environment).resource.toManaged)
}
