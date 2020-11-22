package spotification.playlist

import spotification.authorization.AccessToken
import spotification.common.NonBlankString
import spotification.user.UserId

final case class CreatePlaylistRequest(
  accessToken: AccessToken,
  userId: UserId,
  body: CreatePlaylistRequest.Body
)
object CreatePlaylistRequest {
  final case class Body(
    name: NonBlankString,
    description: Option[NonBlankString]
  )

  def make(
    accessToken: AccessToken,
    userId: UserId,
    anniversaryPlaylistInfo: AnniversaryPlaylistInfo
  ): CreatePlaylistRequest =
    CreatePlaylistRequest(
      accessToken,
      userId,
      Body(anniversaryPlaylistInfo.name, anniversaryPlaylistInfo.description)
    )
}
