package spotification.infra

import org.http4s.client.Client
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.client.middleware.Logger
import shapeless.ops.product.ToMap
import spotification.infra.concurrent.ExecutionContextModule
import zio.interop.catz._
import zio._

import scala.concurrent.ExecutionContext

package object httpclient {
  type H4sClient = Client[Task]
  type H4sAuthorization = org.http4s.headers.Authorization
  type ToMapAux[A] = ToMap.Aux[A, Symbol, Any]
  type ParamMap = Map[String, String]

  type HttpClientModule = Has[H4sClient]
  object HttpClientModule {
    val layer: TaskLayer[HttpClientModule] = {
      val makeHttpClient: URIO[ExecutionContext, RManaged[ExecutionContext, Client[Task]]] =
        ZIO.runtime[ExecutionContext].map(implicit rt => BlazeClientBuilder[Task](rt.environment).resource.toManaged)

      val addLogger: Client[Task] => Client[Task] =
        Logger(logHeaders = true, logBody = true)(_)

      ExecutionContextModule.layer >>>
        ZLayer.fromServiceManaged(makeHttpClient.toManaged_.flatten.map(addLogger).provide)
    }
  }
}
