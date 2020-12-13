package spotification.user

import spotification.authorization.AccessToken
import spotification.common.NonBlankString
import spotification.playlist.AnniversaryPlaylistInfo

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
