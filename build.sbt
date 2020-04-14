val Http4sVersion = "0.21.3"
val CirceVersion = "0.13.0"
val Specs2Version = "4.8.3"
val LogbackVersion = "1.2.3"
val BetterMonadicForVersion = "0.3.1"
val KindProjectorVersion = "0.10.3"
val RefinedVersion = "0.9.13"
val ZIOVersion = "1.0.0-RC18-2"
val ZIOInteropCats = "2.0.0.0-RC12"

lazy val root = (project in file("."))
  .settings(
    organization := "com.github.programaker",
    name := "spotification",
    version := "1.0",
    scalaVersion := "2.13.1",
    libraryDependencies ++= Seq(
      "org.http4s" %% "http4s-blaze-server" % Http4sVersion,
      "org.http4s" %% "http4s-blaze-client" % Http4sVersion,
      "org.http4s" %% "http4s-circe" % Http4sVersion,
      "org.http4s" %% "http4s-dsl" % Http4sVersion,

      "io.circe" %% "circe-generic" % CirceVersion,
      "io.circe" %% "circe-refined" % CirceVersion,

      "ch.qos.logback" % "logback-classic" % LogbackVersion,

      "eu.timepit" %% "refined" % RefinedVersion,

      "dev.zio" %% "zio" % ZIOVersion,
      "dev.zio" %% "zio-interop-cats" % ZIOInteropCats,

      "org.specs2" %% "specs2-core" % Specs2Version % Test
    ),
    addCompilerPlugin("org.typelevel" %% "kind-projector" % KindProjectorVersion),
    addCompilerPlugin("com.olegpy" %% "better-monadic-for" % BetterMonadicForVersion)
  )

ThisBuild / wartremoverWarnings ++= Warts.allBut(
  Wart.Recursion,
  Wart.Nothing,
  Wart.ImplicitParameter,
  Wart.Any,
  Wart.StringPlusAny,
  Wart.Serializable,
  Wart.JavaSerializable
)

ThisBuild / scalacOptions ++= Seq(
  "-encoding", "utf8",
  "-feature",
  "-explaintypes",
  "-deprecation",

  "-language:experimental.macros",
  "-language:existentials",
  "-language:higherKinds",
  "-language:implicitConversions",

  "-Ywarn-dead-code",
  "-Ywarn-value-discard",
  "-Ywarn-unused:imports",
  "-Ywarn-unused:implicits",
  "-Ywarn-unused:locals",
  "-Ywarn-unused:params",
  "-Ywarn-unused:patvars",
  "-Ywarn-unused:privates"
)
