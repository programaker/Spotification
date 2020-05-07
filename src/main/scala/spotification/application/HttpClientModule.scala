package spotification.application

import org.http4s.client.Client
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.client.middleware.Logger
import spotification.application.ExecutionContextModule.ExecutionContextService
import spotification.infra.httpclient.H4sClient
import zio._
import zio.interop.catz._

import scala.concurrent.ExecutionContext

object HttpClientModule {
  type HttpClientService = Has[H4sClient]

  def layer: RLayer[ExecutionContextService, HttpClientService] =
    ZLayer.fromServiceManaged(makeHttpClient.toManaged_.flatten.map(addLogger).provide)

  private def makeHttpClient: URIO[ExecutionContext, RManaged[ExecutionContext, Client[Task]]] =
    ZIO.runtime[ExecutionContext].map(implicit rt => BlazeClientBuilder[Task](rt.environment).resource.toManaged)

  private def addLogger: Client[Task] => Client[Task] =
    Logger(logHeaders = true, logBody = true)(_)
}
