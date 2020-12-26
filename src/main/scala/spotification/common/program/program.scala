package spotification.common

import spotification.authorization.program.AuthorizationProgramsEnv
import spotification.playlist.program.PlaylistProgramsEnv
import spotification.track.program.TrackProgramsEnv

package object program {
  type AllProgramsEnv = AuthorizationProgramsEnv with PlaylistProgramsEnv with TrackProgramsEnv
}
