import com.typesafe.sbt.packager.docker.{Cmd, ExecCmd}

name := """codacy-duplication-flay"""

version := "1.0.0"

val languageVersion = "2.11.7"

scalaVersion := languageVersion

resolvers ++= Seq(
  "Typesafe Repo" at "http://repo.typesafe.com/typesafe/releases/",
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/releases"
)

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-json" % "2.3.10" withSources(),
  "com.codacy" %% "codacy-duplication-scala-seed" % "1.0.2"
)

enablePlugins(JavaAppPackaging)

enablePlugins(DockerPlugin)

version in Docker := "1.0.0"

val installAll =
  s""" apt-get update &&
      |apt-add-repository -y ppa:brightbox/ruby-ng &&
      |apt-get -y update &&
      |apt-get -y install ruby2.2 ruby2.2-dev &&
      |gem install rake hoe sexp_processor ruby_parser ruby2ruby erubis""".stripMargin.replaceAll(System.lineSeparator(), " ")

mappings in Universal <++= (resourceDirectory in Compile) map { (resourceDir: File) =>
  val src = resourceDir / "flay"
  val dest = "/flay"

  for {
    path <- (src ***).get if !path.isDirectory
  } yield path -> path.toString.replaceFirst(src.toString, dest)
}

val dockerUser = "docker"
val dockerGroup = "docker"

daemonUser in Docker := dockerUser

daemonGroup in Docker := dockerGroup

dockerBaseImage := "rtfpessoa/ubuntu-jdk8"

dockerCommands := dockerCommands.value.flatMap {
  case cmd@Cmd("WORKDIR", _) => List(cmd,
    Cmd("RUN", installAll)
  )
  case cmd@(Cmd("ADD", "opt /opt")) => List(cmd,
    Cmd("RUN", "adduser --uid 2004 --disabled-password --gecos \"\" docker")
  )
  case other => List(other)
}
