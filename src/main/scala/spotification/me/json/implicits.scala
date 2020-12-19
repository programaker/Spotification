package spotification.me.json

import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder
import spotification.me.{GetMyFollowedArtistsResponse, GetMyProfileResponse}
import io.circe.refined.refinedDecoder
import spotification.artist.json.implicits.ArtistIdDecoder
import spotification.user.json.implicits.UserIdDecoder

object implicits {
  implicit val GetMyFollowedArtistsResponseArtistDecoder: Decoder[GetMyFollowedArtistsResponse.Artist] = deriveDecoder
  implicit val GetMyFollowedArtistsResponseArtistsDecoder: Decoder[GetMyFollowedArtistsResponse.Artists] = deriveDecoder
  implicit val GetMyFollowedArtistsResponseDecoder: Decoder[GetMyFollowedArtistsResponse] = deriveDecoder
  implicit val GetMyProfileResponseDecoder: Decoder[GetMyProfileResponse] = deriveDecoder
}
