package spotification.me.service

import spotification.me.{
  GetMyFollowedArtistsRequest,
  GetMyFollowedArtistsResponse,
  GetMyProfileRequest,
  GetMyProfileResponse
}
import zio.Task

trait MeService {
  def getMyProfile(req: GetMyProfileRequest): Task[GetMyProfileResponse]
  def getMyFollowedArtists(req: GetMyFollowedArtistsRequest[_]): Task[GetMyFollowedArtistsResponse]
}
