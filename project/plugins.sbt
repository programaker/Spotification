val SbtRevolver = "0.10.0"
val SbtDependencyUpdates = "1.2.7"
val SbtNativePackager = "1.9.16"
val SbtScalaFmt = "2.5.2"

Seq(
  "io.spray" % "sbt-revolver" % SbtRevolver,
  "org.jmotor.sbt" % "sbt-dependency-updates" % SbtDependencyUpdates,
  "com.github.sbt" % "sbt-native-packager" % SbtNativePackager,
  "org.scalameta" % "sbt-scalafmt" % SbtScalaFmt
).map(addSbtPlugin)
