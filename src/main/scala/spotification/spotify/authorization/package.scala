package spotification.spotify

import eu.timepit.refined.api.Refined
import spotification.NonBlankString
import zio.Has

package object authorization {
  type ClientId = String Refined NonBlankString
  type Authorization = Has[AuthorizationService]
}
