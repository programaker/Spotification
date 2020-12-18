val SpotificationV = "3.2.14"
val ScalaV = "2.13.4"
val DockerImageV = "adoptopenjdk/openjdk15-openj9:x86_64-alpine-jre-15.0.1_9_openj9-0.23.0"

val Http4sV = "0.21.12"
val CirceV = "0.13.0"
val Specs2V = "4.10.5"
val LogbackV = "1.2.3"
val BetterMonadicForV = "0.3.1"
val KindProjectorV = "0.10.3"
val RefinedV = "0.9.19"
val ZioV = "1.0.3"
val ZioInteropCatsV = "2.2.0.1"
val PureConfigV = "0.14.0"
val SimulacrumV = "1.0.1"
val NewtypeV = "0.4.4"
val OdinV = "0.9.1"

lazy val root = (project in file(".")).settings(
  organization := "com.github.programaker",
  name := "spotification",
  version := SpotificationV,
  scalaVersion := ScalaV,

  libraryDependencies ++= Seq(
    "org.http4s" %% "http4s-blaze-server" % Http4sV,
    "org.http4s" %% "http4s-blaze-client" % Http4sV,
    "org.http4s" %% "http4s-circe" % Http4sV,
    "org.http4s" %% "http4s-dsl" % Http4sV,

    "io.circe" %% "circe-generic" % CirceV,
    "io.circe" %% "circe-refined" % CirceV,

    "ch.qos.logback" % "logback-classic" % LogbackV,

    "com.github.valskalla" %% "odin-core" % OdinV,
    "com.github.valskalla" %% "odin-zio" % OdinV,

    "eu.timepit" %% "refined" % RefinedV,
    "eu.timepit" %% "refined-cats" % RefinedV,
    "eu.timepit" %% "refined-pureconfig" % RefinedV,

    "io.estatico" %% "newtype" % NewtypeV,

    "dev.zio" %% "zio" % ZioV,
    "dev.zio" %% "zio-interop-cats" % ZioInteropCatsV,

    "com.github.pureconfig" %% "pureconfig" % PureConfigV,

    "org.typelevel" %% "simulacrum" % SimulacrumV,

    "org.specs2" %% "specs2-core" % Specs2V % Test
  ),

  Seq(
    "org.typelevel" %% "kind-projector" % KindProjectorV,
    "com.olegpy" %% "better-monadic-for" % BetterMonadicForV
  ).map(addCompilerPlugin)
)

enablePlugins(
  JavaServerAppPackaging,
  DockerPlugin,
  AshScriptPlugin
)

ThisBuild / wartremoverErrors ++= Seq(
  Wart.FinalCaseClass,
  Wart.Throw,
  Wart.Return
)
ThisBuild / wartremoverWarnings ++= Warts.allBut(
  Wart.Recursion,
  Wart.ImplicitParameter,
  Wart.Any,
  Wart.Nothing,
  Wart.ImplicitConversion,
  Wart.Overloading
)

// disable Wartremover in console. Not only it's unnecessary but also cause error in Scala 2.13.2+
Compile / console / scalacOptions := (console / scalacOptions).value.filterNot(_.contains("wartremover"))

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
  "-Ywarn-unused:explicits",
  "-Ywarn-unused:locals",
  "-Ywarn-unused:params",
  "-Ywarn-unused:patvars",
  "-Ywarn-unused:privates",

  "-Ymacro-annotations"
)

mainClass in Compile := Some("spotification.SpotificationHttpApp")

dockerBaseImage := DockerImageV
dockerExposedPorts += 8080
