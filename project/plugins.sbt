Seq(
  // For rapid application development. Start the server from the SBT prompt with `reStart`
  "io.spray" % "sbt-revolver" % "0.9.1",

  "org.wartremover" % "sbt-wartremover" % "2.4.6",
  "com.timushev.sbt" % "sbt-updates" % "0.5.0"
).map(addSbtPlugin)
