val SbtRevolver = "0.9.1"
val WartRemover = "2.4.16"
val SbtDependencyUpdates = "1.2.2"
val SbtNativePackager = "1.8.1"
val SbtScalaFmt = "2.4.2"

Seq(
  "io.spray" % "sbt-revolver" % SbtRevolver,
  "org.wartremover" % "sbt-wartremover" % WartRemover,
  "org.jmotor.sbt" % "sbt-dependency-updates" % SbtDependencyUpdates,
  "com.typesafe.sbt" % "sbt-native-packager" % SbtNativePackager,
  "org.scalameta" % "sbt-scalafmt" % SbtScalaFmt
).map(addSbtPlugin)
