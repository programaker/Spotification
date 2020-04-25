val sbtRevolverV = "0.9.1"
val wartRemoverV = "2.4.7"
val sbtUpdatesV = "0.5.0"
val sbtDotEnvV = "2.1.146"

Seq(
  "io.spray" % "sbt-revolver" % sbtRevolverV,
  "org.wartremover" % "sbt-wartremover" % wartRemoverV,
  "com.timushev.sbt" % "sbt-updates" % sbtUpdatesV,
  "au.com.onegeek" %% "sbt-dotenv" % sbtDotEnvV
).map(addSbtPlugin)
