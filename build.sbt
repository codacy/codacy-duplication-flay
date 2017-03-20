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

dockerBaseImage := "develar/java"

val installAll =
  s"""apk --no-cache add bash build-base ruby ruby-dev &&
     |apk add --update ca-certificates &&
     |gem install --no-ri --no-rdoc rake hoe sexp_processor ruby_parser ruby2ruby erubis &&
     |gem cleanup &&
     |apk del build-base ruby-dev &&
     |rm -rf /tmp/* &&
     |rm -rf /var/cache/apk/*""".stripMargin.replaceAll(System.lineSeparator(), " ")

dockerCommands := dockerCommands.value.flatMap {
  case cmd@Cmd("WORKDIR", _) => List(cmd,
    Cmd("RUN", installAll)
  )

  case cmd@(Cmd("ADD", "opt /opt")) => List(cmd,
    Cmd("RUN", s"adduser -u 2004 -D $dockerUser")
  )
  case other => List(other)
}
