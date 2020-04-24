package spotification.infra

import eu.timepit.refined.api.Refined
import eu.timepit.refined.auto._
import org.http4s.client.Client
import org.http4s.client.dsl.Http4sClientDsl
import shapeless.ops.product.ToMap
import spotification.core._
import spotification.core.spotify.authorization._
import zio.Task

package object httpclient extends AuthorizationM {

  type H4sClient = Client[Task]
  type ToMapAux[A] = ToMap.Aux[A, Symbol, Any]
  type ParamMap = Map[String, String]

  val H4sTaskClientDsl: Http4sClientDsl[Task] = new Http4sClientDsl[Task] {}

  /**
   * <p>Turns any Product type (ex: case classes) into a `Map[String, String]` that can be
   * used to build query string parameters or x-www-form-urlencoded.</p>
   * <p></p>
   * <p>The function only acts on fields of type `String` (required fields)
   * and `Option[String]` (optional fields); everything else will be ignored.</p>
   */
  def toParams[A <: Product](a: A)(implicit toMap: ToMapAux[A]): ParamMap =
    toMap(a).flatMap {
      case (k, v: String)        => Some(k.name -> encode(v))
      case (k, Some(v: String))  => Some(k.name -> encode(v))
      case (k, v: Refined[_, _]) => Some(k.name -> encode(s"$v"))
      case _                     => None
    }

  def addScopeParam(params: ParamMap, scopes: List[Scope]): Either[String, ParamMap] =
    joinScopes(scopes).map(s => params + ("scope" -> s))

}
