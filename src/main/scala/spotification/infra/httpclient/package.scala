package spotification.infra

import org.http4s.client.Client
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.client.middleware.Logger
import shapeless.ops.product.ToMap
import spotification.infra.concurrent.ExecutionContextModule
import zio.{Has, RLayer, RManaged, Task, URIO, ZIO, ZLayer}
import zio.interop.catz._

import scala.concurrent.ExecutionContext

package object httpclient {
  type H4sClient = Client[Task]
  type ToMapAux[A] = ToMap.Aux[A, Symbol, Any]
  type ParamMap = Map[String, String]

  type H4sAuthorization = org.http4s.headers.Authorization
  val H4sAuthorization: org.http4s.headers.Authorization.type = org.http4s.headers.Authorization

  val H4sTaskClientDsl: Http4sClientDsl[Task] = new Http4sClientDsl[Task] {}

  type HttpClientModule = Has[H4sClient]
  object HttpClientModule {
    val layer: RLayer[ExecutionContextModule, HttpClientModule] = {
      val makeHttpClient: URIO[ExecutionContext, RManaged[ExecutionContext, Client[Task]]] =
        ZIO.runtime[ExecutionContext].map(implicit rt => BlazeClientBuilder[Task](rt.environment).resource.toManaged)

      val addLogger: Client[Task] => Client[Task] =
        Logger(logHeaders = true, logBody = true)(_)

      ZLayer.fromServiceManaged(makeHttpClient.toManaged_.flatten.map(addLogger).provide)
    }
  }
}
