package spotification.spotifyapi.authorization

sealed abstract class Scope(val name: String)

object Scope {
  object Playlists {
    object ReadCollaborative extends Scope("playlist-read-collaborative")
    object ModifyPublic extends Scope("playlist-modify-public")
    object ReadPrivate extends Scope("playlist-read-private")
    object ModifyPrivate extends Scope("playlist-modify-private")
  }
}
