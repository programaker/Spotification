import sbt._

object Dependencies {
  import Versions._

  val Http4sBlazeServer = "org.http4s" %% "http4s-blaze-server" % Versions.Http4s
  val Http4sBlazeClient = "org.http4s" %% "http4s-blaze-client" % Versions.Http4s
  val Http4sCirce = "org.http4s" %% "http4s-circe" % Versions.Http4s
  val Http4sDsl = "org.http4s" %% "http4s-dsl" % Versions.Http4s

  val CirceGeneric = "io.circe" %% "circe-generic" % Versions.Circe
  val CirceRefined = "io.circe" %% "circe-refined" % Versions.Circe

  val LogbackClassic = "ch.qos.logback" % "logback-classic" % Versions.Logback

  val OdinCore = "com.github.valskalla" %% "odin-core" % Versions.Odin
  val OdinZio = "com.github.valskalla" %% "odin-zio" % Versions.Odin

  val Refined = "eu.timepit" %% "refined" % Versions.Refined
  val RefinedCats = "eu.timepit" %% "refined-cats" % Versions.Refined
  val RefinedPureconfig = "eu.timepit" %% "refined-pureconfig" % Versions.Refined

  val Newtype = "io.estatico" %% "newtype" % Versions.Newtype

  val Zio = "dev.zio" %% "zio" % Versions.Zio
  val ZioInteropCats = "dev.zio" %% "zio-interop-cats" % Versions.ZioInteropCats

  val Pureconfig = "com.github.pureconfig" %% "pureconfig" % Versions.PureConfig

  val Simulacrum = "org.typelevel" %% "simulacrum" % Versions.Simulacrum

  val MonocleCore = "com.github.julien-truffaut" %% "monocle-core" % Versions.Monocle
  val MonocleMacro = "com.github.julien-truffaut" %% "monocle-macro" % Versions.Monocle
  val MonocleRefined = "com.github.julien-truffaut" %% "monocle-refined" % Versions.Monocle

  val KindProjector = "org.typelevel" %% "kind-projector" % Versions.KindProjector
  val BetterMonadicFor = "com.olegpy" %% "better-monadic-for" % Versions.BetterMonadicFor

  val Specs2Core = "org.specs2" %% "specs2-core" % Versions.Specs2Core % Test
}

private object Versions {
  val Http4s = "0.23.6"
  val Circe = "0.14.1"
  val Specs2Core = "4.13.0"
  val Logback = "1.2.7"
  val BetterMonadicFor = "0.3.1"
  val KindProjector = "0.10.3"
  val Refined = "0.9.27"
  val Zio = "1.0.12"
  val ZioInteropCats = "3.1.1.0"
  val PureConfig = "0.17.0"
  val Simulacrum = "1.0.1"
  val Newtype = "0.4.4"
  val Odin = "0.13.0"
  val Monocle = "2.1.0"
}
