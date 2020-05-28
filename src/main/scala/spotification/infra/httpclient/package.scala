package spotification.infra

import org.http4s.client.Client
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.client.middleware.Logger
import shapeless.ops.product.ToMap
import spotification.domain.config.ClientConfig
import spotification.infra.concurrent.ExecutionContextModule
import spotification.infra.config.ClientConfigModule
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

      def addLogger(config: ClientConfig): Client[Task] => Client[Task] =
        Logger(config.logHeaders, config.logBody)(_)

      val l = ZLayer.fromServicesManaged[ExecutionContext, ClientConfig, Any, Throwable, H4sClient] { (ex, config) =>
        makeHttpClient.toManaged_.flatten.map(addLogger(config)).provide(ex)
      }

      (ExecutionContextModule.layer ++ ClientConfigModule.layer) >>> l
    }
  }
}
