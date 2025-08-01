// Resolvers
resolvers += Resolver.jcenterRepo

libraryDependencies ++= Seq(
  "com.github.pathikrit" %% "better-files" % "3.9.2",
  "org.playframework" %% "play-json" % "3.0.5")

dependencyOverrides += "org.scala-lang.modules" %% "scala-xml" % "2.4.0"

// Static Analysis
addSbtPlugin("com.sksamuel.scapegoat" % "sbt-scapegoat" % "1.2.13")
addSbtPlugin("ch.epfl.scala" % "sbt-scalafix" % "0.14.3")

// Dependencies
addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.6.3")
addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.9.2")

// Coverage
addSbtPlugin("org.scoverage" % "sbt-scoverage" % "2.3.1")
addSbtPlugin("com.codacy" % "sbt-codacy-coverage" % "3.1.120")

addSbtPlugin("com.codacy" % "codacy-sbt-plugin" % "25.2.4")

addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.5.5")
