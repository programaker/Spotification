package spotification.spotify

import zio.{Has, RIO, ZIO}

package object authorization {
  type Authorization = Has[Authorization.Service]

  object Authorization {
    trait Service {
      def authorize(req: AuthorizationRequest): ZIO[Credentials, AuthorizationError, AuthorizationResponse]
      def requestToken(req: TokenRequest): RIO[Credentials, TokenResponse]
      def refreshToken(req: RefreshTokenRequest): RIO[Credentials, RefreshTokenResponse]
    }
  }
}
