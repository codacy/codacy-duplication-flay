import sbt._

object Dependencies {

  object Codacy {
    val duplicationSeed = "com.codacy" %% "codacy-duplication-scala-seed" % "2.0.1"
  }

  val specs2Version = "4.2.0"
  val specs2 = "org.specs2" %% "specs2-core" % specs2Version
}
