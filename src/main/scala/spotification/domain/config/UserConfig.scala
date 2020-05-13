package spotification.domain.config

import spotification.domain.spotify.user.{UserApiUri, UserId}

final case class UserConfig(
  userId: UserId,
  userApiUri: UserApiUri
)
