package spotification.spotify

import zio.{Has, ZIO}

package object authorization {
  type Authorization = Has[Authorization.Service]

  object Authorization {
    trait Service {
      def authorize(req: AuthorizationRequest): ZIO[Credentials, AuthorizationError, AuthorizationResponse]
      def apiToken(req: ApiTokenRequest): ZIO[Credentials, Nothing, Nothing]
    }
  }
}
