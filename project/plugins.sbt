val SbtRevolver = "0.9.1"
val SbtDependencyUpdates = "1.2.2"
val SbtNativePackager = "1.9.9"
val SbtScalaFmt = "2.4.6"

Seq(
  "io.spray" % "sbt-revolver" % SbtRevolver,
  "org.jmotor.sbt" % "sbt-dependency-updates" % SbtDependencyUpdates,
  "com.github.sbt" % "sbt-native-packager" % SbtNativePackager,
  "org.scalameta" % "sbt-scalafmt" % SbtScalaFmt
).map(addSbtPlugin)
