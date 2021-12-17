import sbt._

object Dependencies {
  val libraries = Seq(
    "org.http4s" %% "http4s-blaze-server" % Versions.Http4s,
    "org.http4s" %% "http4s-blaze-client" % Versions.Http4s,
    "org.http4s" %% "http4s-circe" % Versions.Http4s,
    "org.http4s" %% "http4s-dsl" % Versions.Http4s,

    "io.circe" %% "circe-generic" % Versions.Circe,
    "io.circe" %% "circe-refined" % Versions.Circe,

    "com.github.valskalla" %% "odin-core" % Versions.Odin,
    "com.github.valskalla" %% "odin-zio" % Versions.Odin,

    "eu.timepit" %% "refined" % Versions.Refined,
    "eu.timepit" %% "refined-cats" % Versions.Refined,
    "eu.timepit" %% "refined-pureconfig" % Versions.Refined,

    "dev.zio" %% "zio" % Versions.Zio,
    "dev.zio" %% "zio-interop-cats" % Versions.ZioInteropCats,

    "com.github.julien-truffaut" %% "monocle-core" % Versions.Monocle,
    "com.github.julien-truffaut" %% "monocle-macro" % Versions.Monocle,
    "com.github.julien-truffaut" %% "monocle-refined" % Versions.Monocle,

    "ch.qos.logback" % "logback-classic" % Versions.Logback,
    "io.estatico" %% "newtype" % Versions.Newtype,
    "com.github.pureconfig" %% "pureconfig" % Versions.PureConfig,
    "org.typelevel" %% "simulacrum" % Versions.Simulacrum
  )

  val compilerPlugins = Seq(
    "org.typelevel" %% "kind-projector" % Versions.KindProjector,
    "com.olegpy" %% "better-monadic-for" % Versions.BetterMonadicFor
  )
}

private object Versions {
  val Http4s = "0.23.7"
  val Circe = "0.14.1"
  val Specs2Core = "4.13.1"
  val Logback = "1.2.9"
  val BetterMonadicFor = "0.3.1"
  val KindProjector = "0.10.3"
  val Refined = "0.9.28"
  val Zio = "1.0.13"
  val ZioInteropCats = "3.2.9.0"
  val PureConfig = "0.17.1"
  val Simulacrum = "1.0.1"
  val Newtype = "0.4.4"
  val Odin = "0.13.0"
  val Monocle = "2.1.0"
}
