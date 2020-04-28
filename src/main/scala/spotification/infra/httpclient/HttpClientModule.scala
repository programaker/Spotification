package spotification.infra.httpclient

import org.http4s.client.Client
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.client.middleware.{FollowRedirect, Logger}
import spotification.infra.concurrent.ExecutionContextModule.ExecutionContextService
import zio._
import zio.interop.catz._

import scala.concurrent.ExecutionContext

object HttpClientModule {
  type HttpClientService = Has[H4sClient]

  val layer: RLayer[ExecutionContextService, HttpClientService] =
    ZLayer.fromServiceManaged(makeHttpClient.toManaged_.flatten.map(addFollowRedirect compose addLogger).provide)

  private def makeHttpClient: URIO[ExecutionContext, RManaged[ExecutionContext, Client[Task]]] =
    ZIO.runtime[ExecutionContext].map(implicit rt => BlazeClientBuilder[Task](rt.environment).resource.toManaged)

  private def addLogger: Client[Task] => Client[Task] =
    Logger(logHeaders = true, logBody = true)(_)

  private def addFollowRedirect: Client[Task] => Client[Task] =
    FollowRedirect(maxRedirects = 5)(_) //TODO => move maxRedirects to config
}
