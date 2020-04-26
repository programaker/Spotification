package spotification.infra

import org.http4s.client.Client
import org.http4s.client.dsl.Http4sClientDsl
import shapeless.ops.product.ToMap
import zio.Task

package object httpclient {
  type H4sClient = Client[Task]
  type ToMapAux[A] = ToMap.Aux[A, Symbol, Any]
  type ParamMap = Map[String, String]

  type H4sAuthorization = org.http4s.headers.Authorization
  val H4sAuthorization: org.http4s.headers.Authorization.type = org.http4s.headers.Authorization

  val H4sTaskClientDsl: Http4sClientDsl[Task] = new Http4sClientDsl[Task] {}
}
