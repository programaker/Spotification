val sbtRevolverV = "0.9.1"
val wartRemoverV = "2.4.7"
val sbtUpdatesV = "0.5.0"
val sbtDotEnvV = "2.1.146"
val sbtNativePackagerV = "1.7.2"
val sbtScalaFmtV = "2.4.0"

Seq(
  "io.spray" % "sbt-revolver" % sbtRevolverV,
  "org.wartremover" % "sbt-wartremover" % wartRemoverV,
  "com.timushev.sbt" % "sbt-updates" % sbtUpdatesV,
  "com.typesafe.sbt" % "sbt-native-packager" % sbtNativePackagerV,
  "org.scalameta" % "sbt-scalafmt" % sbtScalaFmtV,
  "au.com.onegeek" %% "sbt-dotenv" % sbtDotEnvV
).map(addSbtPlugin)
