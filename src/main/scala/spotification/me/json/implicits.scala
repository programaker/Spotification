package spotification.me.json

import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder
import spotification.me.GetMyFollowedArtistsResponse
import io.circe.refined.refinedDecoder
import spotification.artist.json.implicits.ArtistIdDecoder

object implicits {
  implicit val GetMyFollowedArtistsResponseArtistDecoder: Decoder[GetMyFollowedArtistsResponse.Artist] = deriveDecoder
  implicit val GetMyFollowedArtistsResponseArtistsDecoder: Decoder[GetMyFollowedArtistsResponse.Artists] = deriveDecoder
  implicit val GetMyFollowedArtistsResponseDecoder: Decoder[GetMyFollowedArtistsResponse] = deriveDecoder
}
