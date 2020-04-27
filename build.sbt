val http4sV = "0.21.3"
val circeV = "0.13.0"
val specs2V = "4.9.3"
val logbackV = "1.2.3"
val betterMonadicForV = "0.3.1"
val kindProjectorV = "0.10.3"
val refinedV = "0.9.14"
val zioV = "1.0.0-RC18-2"
val zioInteropCatsV = "2.0.0.0-RC12"
val pureConfigV = "0.12.3"
val simulacrumV = "1.0.0"
val newtypeV = "0.4.3"
val odinV = "0.7.0"

lazy val root = (project in file("."))
  .settings(
    organization := "com.github.programaker",
    name := "spotification",
    version := "1.0",
    scalaVersion := "2.13.1",

    libraryDependencies ++= Seq(
      "org.http4s" %% "http4s-blaze-server" % http4sV,
      "org.http4s" %% "http4s-blaze-client" % http4sV,
      "org.http4s" %% "http4s-circe" % http4sV,
      "org.http4s" %% "http4s-dsl" % http4sV,

      "io.circe" %% "circe-generic" % circeV,
      "io.circe" %% "circe-refined" % circeV,

      "ch.qos.logback" % "logback-classic" % logbackV,

      "com.github.valskalla" %% "odin-core" % odinV,
      "com.github.valskalla" %% "odin-zio" % odinV,

      "eu.timepit" %% "refined" % refinedV,
      "eu.timepit" %% "refined-cats" % refinedV,
      "eu.timepit" %% "refined-pureconfig" % refinedV,

      "io.estatico" %% "newtype" % newtypeV,

      "dev.zio" %% "zio" % zioV,
      "dev.zio" %% "zio-interop-cats" % zioInteropCatsV,

      "com.github.pureconfig" %% "pureconfig" % pureConfigV,

      "org.typelevel" %% "simulacrum" % simulacrumV,

      "org.specs2" %% "specs2-core" % specs2V % Test
    ),
    
    Seq(
      "org.typelevel" %% "kind-projector" % kindProjectorV,
      "com.olegpy" %% "better-monadic-for" % betterMonadicForV
    ).map(addCompilerPlugin)
  )

ThisBuild / wartremoverWarnings ++= Warts.allBut(
  Wart.Recursion,
  Wart.Nothing,
  Wart.ImplicitParameter,
  Wart.Any,
  Wart.StringPlusAny,
  Wart.ImplicitConversion,
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
  "-Ywarn-unused:explicits",
  "-Ywarn-unused:locals",
  "-Ywarn-unused:params",
  "-Ywarn-unused:patvars",
  "-Ywarn-unused:privates",

  "-Ymacro-annotations"
)
