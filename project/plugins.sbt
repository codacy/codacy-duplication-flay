// Resolvers
resolvers ++= Seq(
  "Typesafe Repo" at "http://repo.typesafe.com/typesafe/releases/",
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/releases")

libraryDependencies ++= Seq("com.github.pathikrit" %% "better-files" % "3.6.0",
  "com.typesafe.play" %% "play-json" % "2.6.9")

// Static Analysis
addSbtPlugin("com.sksamuel.scapegoat" %% "sbt-scapegoat" % "1.0.7")
addSbtPlugin("ch.epfl.scala" % "sbt-scalafix" % "0.5.10")

// Formating
addSbtPlugin("com.geirsson" % "sbt-scalafmt" % "1.4.0")

// Dependencies
addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.3.4")
addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.9.0")

// Packaging (Docker)
addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.3.6")

// Coverage
addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.5.1")
addSbtPlugin("com.codacy" % "sbt-codacy-coverage" % "1.3.11")
