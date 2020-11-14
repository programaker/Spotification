val SbtRevolverV = "0.9.1"
val WartRemoverV = "2.4.13"
val SbtDependencyUpdatesV = "1.2.2"
val SbtNativePackagerV = "1.7.5"
val SbtScalaFmtV = "2.4.2"
val SbtDotEnvV = "2.1.146"

Seq(
  "io.spray" % "sbt-revolver" % SbtRevolverV,
  "org.wartremover" % "sbt-wartremover" % WartRemoverV,
  "org.jmotor.sbt" % "sbt-dependency-updates" % SbtDependencyUpdatesV,
  "com.typesafe.sbt" % "sbt-native-packager" % SbtNativePackagerV,
  "org.scalameta" % "sbt-scalafmt" % SbtScalaFmtV,
  "au.com.onegeek" %% "sbt-dotenv" % SbtDotEnvV
).map(addSbtPlugin)
