val Spotification = "3.3.5"
val Scala = "2.13.8"
val DockerImage = "bellsoft/liberica-openjre-alpine:17.0.1"
val MainClass = "spotification.SpotificationHttpApp"

lazy val root = project.in(file("."))
  .settings(
    organization := "com.github.programaker",
    name := "spotification",
    version := Spotification,
    scalaVersion := Scala,

    libraryDependencies ++= Dependencies.libraries,
    Dependencies.compilerPlugins.map(addCompilerPlugin),

    wartremoverErrors ++= Seq(
      Wart.FinalCaseClass,
      Wart.Throw,
      Wart.Return
    ),
    wartremoverWarnings ++= Warts.allBut(
      Wart.Recursion,
      Wart.ImplicitParameter,
      Wart.Any,
      Wart.Nothing,
      Wart.ImplicitConversion,
      Wart.Overloading,
      Wart.JavaSerializable,
      Wart.Serializable,
      Wart.Product
    ),
    // disable Wartremover in console. Not only it's unnecessary but also cause error in Scala 2.13.2+
    Compile / console / scalacOptions := (console / scalacOptions).value.filterNot(_.contains("wartremover")),

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
    DockerPlugin,
    AshScriptPlugin
  )
