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
        scalacOptions ++= Common.compilerFlags,
        scalacOptions in Test ++= Seq("-Yrangepos"),
        scalacOptions in (Compile, console) --= Seq("-Ywarn-unused:imports", "-Xfatal-warnings"))),
    name := "codacy-duplication-flay",
    // App Dependencies
    libraryDependencies ++= Seq(Dependencies.Codacy.duplicationSeed),
    // Test Dependencies
    libraryDependencies ++= Seq(Dependencies.specs2).map(_ % Test))
  .settings(Common.dockerSettings: _*)

scalaVersion in ThisBuild := scalaVersionNumber
scalaBinaryVersion in ThisBuild := scalaBinaryVersionNumber

scapegoatVersion in ThisBuild := "1.3.5"
