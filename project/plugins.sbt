// Resolvers
resolvers ++= Seq(
  "Typesafe Repo" at "http://repo.typesafe.com/typesafe/releases/",
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/releases")

libraryDependencies ++= Seq("com.github.pathikrit" %% "better-files" % "3.6.0",
  "com.typesafe.play" %% "play-json" % "2.6.9")

// Static Analysis
addSbtPlugin("com.sksamuel.scapegoat" %% "sbt-scapegoat" % "1.0.7")
addSbtPlugin("ch.epfl.scala" % "sbt-scalafix" % "0.5.10")

// Dependencies
addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.3.4")
addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.9.0")

// Coverage
addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.5.1")
addSbtPlugin("com.codacy" % "sbt-codacy-coverage" % "1.3.11")

resolvers += Resolver.jcenterRepo
addSbtPlugin("com.codacy" % "codacy-sbt-plugin" % "17.1.5")
