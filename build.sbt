import sbt.Keys._
import sbt._

val scalaBinaryVersionNumber = "2.12"
val scalaVersionNumber = s"$scalaBinaryVersionNumber.4"

lazy val codacyDuplicationFlay = project
  .in(file("."))
  .enablePlugins(JavaAppPackaging)
  .enablePlugins(DockerPlugin)
  .settings(
    inThisBuild(
      List(
        organization := "com.codacy",
        scalaVersion := scalaVersionNumber,
        version := "0.1.0-SNAPSHOT",
        resolvers := Seq("Sonatype OSS Snapshots".at("https://oss.sonatype.org/content/repositories/releases")) ++ resolvers.value,
        scalacOptions ++= Common.compilerFlags,
        scalacOptions in Test ++= Seq("-Yrangepos"),
        scalacOptions in (Compile, console) --= Seq("-Ywarn-unused:imports", "-Xfatal-warnings"))),
    name := "codacy-duplication-flay",
    // App Dependencies
    libraryDependencies ++= Seq(Dependencies.Codacy.duplicationSeed),
    // Test Dependencies
    libraryDependencies ++= Seq(Dependencies.specs2).map(_ % Test))
  .settings(Common.dockerSettings: _*)

mappings in Universal ++= {
  val rubyFiles = Seq(
    (file("Gemfile"), "/setup/Gemfile"),
    (file("Gemfile.lock"), "/setup/Gemfile.lock"),
    (file(".ruby-version"), "/setup/.ruby-version"),
    (file("src/main/resources/flay/bin/flay"), "/flay/bin/flay"),
    (file("src/main/resources/flay/lib/flay.rb"), "/flay/lib/flay.rb"),
    (file("src/main/resources/flay/lib/flay_erb.rb"), "/flay/lib/flay_erb.rb"),
    (file("src/main/resources/flay/lib/flay_task.rb"), "/flay/lib/flay_task.rb"),
    (file("src/main/resources/flay/lib/gauntlet_flay.rb"), "/flay/lib/gauntlet_flay.rb"),
    (file("src/main/resources/flay/test/test_flay.rb"), "/flay/test/test_flay.rb"),
    (file("src/main/resources/flay/History.txt"), "/flay/History.txt"),
    (file("src/main/resources/flay/Manifest.txt"), "/flay/Manifest.txt"),
    (file("src/main/resources/flay/Rakefile"), "/flay/Rakefile"),
    (file("src/main/resources/flay/README.txt"), "/flay/README.txt"))

  rubyFiles
}

scalaVersion in ThisBuild := scalaVersionNumber
scalaBinaryVersion in ThisBuild := scalaBinaryVersionNumber

scapegoatVersion in ThisBuild := "1.3.5"
