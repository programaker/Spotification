val sbtRevolverV = "0.9.1"
val wartRemoverV = "2.4.13"
val sbtDependencyUpdatesV = "1.2.2"
val sbtNativePackagerV = "1.7.5"
val sbtScalaFmtV = "2.4.2"
val sbtDotEnvV = "2.1.146"

Seq(
  "io.spray" % "sbt-revolver" % sbtRevolverV,
  "org.wartremover" % "sbt-wartremover" % wartRemoverV,
  "org.jmotor.sbt" % "sbt-dependency-updates" % sbtDependencyUpdatesV,
  "com.typesafe.sbt" % "sbt-native-packager" % sbtNativePackagerV,
  "org.scalameta" % "sbt-scalafmt" % sbtScalaFmtV,
  "au.com.onegeek" %% "sbt-dotenv" % sbtDotEnvV
).map(addSbtPlugin)
