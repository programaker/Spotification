package spotification.common

import spotification.authorization.program.AuthorizationProgramsR
import spotification.playlist.program.PlaylistProgramsR
import spotification.track.program.TrackProgramsR

package object program {
  type AllProgramsR = AuthorizationProgramsR with PlaylistProgramsR with TrackProgramsR
}
