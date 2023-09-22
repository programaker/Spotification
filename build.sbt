val Spotification = "3.3.7"
val Scala = "2.13.12"
val DockerImage = "eclipse-temurin:19.0.1_10-jre-focal"
val MainClass = "spotification.SpotificationHttpApp"

lazy val root = project.in(file("."))
  .settings(
    organization := "com.github.programaker",
    name := "spotification",
    version := Spotification,
    scalaVersion := Scala,

    libraryDependencies ++= Dependencies.libraries,
    Dependencies.compilerPlugins.map(addCompilerPlugin),

    scalacOptions ++= Seq(
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
    ),

    Compile / mainClass := Some(MainClass),

    dockerBaseImage := DockerImage,
    dockerExposedPorts += 8080
  )
  .enablePlugins(
    JavaServerAppPackaging,
    DockerPlugin
  )
