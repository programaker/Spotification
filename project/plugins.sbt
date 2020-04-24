val sbtRevolverV = "0.9.1"
val wartRemoverV = "2.4.7"
val sbtUpdatesV = "0.5.0"

Seq(
  // For rapid application development. Start the server from the SBT prompt with `reStart`
  "io.spray" % "sbt-revolver" % sbtRevolverV,

  "org.wartremover" % "sbt-wartremover" % wartRemoverV,
  "com.timushev.sbt" % "sbt-updates" % sbtUpdatesV
).map(addSbtPlugin)
