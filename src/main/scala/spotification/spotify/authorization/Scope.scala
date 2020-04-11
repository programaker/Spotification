package spotification.spotify.authorization

final class Scope private (val name: String) extends AnyVal

object Scope {
  object Playlists {
    val ReadCollaborative: Scope = new Scope("playlist-read-collaborative")
    val ModifyPublic: Scope = new Scope("playlist-modify-public")
    val ReadPrivate: Scope = new Scope("playlist-read-private")
    val ModifyPrivate: Scope = new Scope("playlist-modify-private")
  }
}
