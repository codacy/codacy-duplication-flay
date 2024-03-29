import com.typesafe.sbt.packager.Keys._
import com.typesafe.sbt.packager.docker.DockerPlugin.autoImport.Docker
import com.typesafe.sbt.packager.docker.{Cmd, DockerAlias}
import sbt.Keys._
import sbt.{Def, _}

object Common {

  private val defaultDockerInstallationPath = "/opt/codacy"

  val dockerSettings: Seq[Def.Setting[_]] = Seq(
    packageName in Docker := packageName.value,
    version in Docker := version.value,
    maintainer in Docker := "Codacy <team@codacy.com>",
    dockerBaseImage := "amazoncorretto:8-alpine3.17-jre",
    dockerUpdateLatest := true,
    defaultLinuxInstallLocation in Docker := defaultDockerInstallationPath,
    daemonUser in Docker := "docker",
    dockerEntrypoint := Seq(s"$defaultDockerInstallationPath/bin/${name.value}"),
    dockerCmd := Seq(),
    dockerCommands := dockerCommands.value.flatMap {
      case cmd @ Cmd("ADD", "--chown=docker:docker opt /opt") =>
        List(
          cmd,
          Cmd("RUN", "mv /opt/codacy/docs /docs"),
          Cmd("RUN",
            s"""|apk add --no-cache bash ruby ruby-irb ruby-rake ruby-io-console ruby-bigdecimal
                |   ruby-json ruby-bundler libstdc++ tzdata bash ca-certificates libc-dev
                |&& echo 'gem: --no-document' > /etc/gemrc
                |&& gem install rake -v 12.3.3
                |&& gem install rdoc
                |&& cd $defaultDockerInstallationPath/setup && bundle install && gem cleanup
                |&& rm -rf /tmp/* /var/cache/apk/*""".stripMargin.replaceAll(System.lineSeparator(), " "))
        )

      case cmd @ Cmd("WORKDIR", _) =>
        List(
          Cmd("RUN", "adduser -u 2004 -D docker"),
          Cmd("ENV", s"FLAY_SOURCE_PATH $defaultDockerInstallationPath/flay"),
          cmd)

      case other => List(other)
    })

  val compilerFlags: Seq[String] = Seq(
    "-deprecation", // Emit warning and location for usages of deprecated APIs.
    "-encoding",
    "utf-8", // Specify character encoding used by source files.
    "-explaintypes", // Explain type errors in more detail.
    "-feature", // Emit warning and location for usages of features that should be imported explicitly.
    "-language:existentials", // Existential types (besides wildcard types) can be written and inferred
    "-language:experimental.macros", // Allow macro definition (besides implementation and application)
    "-language:higherKinds", // Allow higher-kinded types
    "-language:implicitConversions", // Allow definition of implicit functions called views
    "-language:postfixOps",
    "-unchecked", // Enable additional warnings where generated code depends on assumptions.
    "-Xcheckinit", // Wrap field accessors to throw an exception on uninitialized access.
    "-Xfatal-warnings", // Fail the compilation if there are any warnings.
    "-Xfuture", // Turn on future language features.
    "-Xlint",
    "-Xlint:adapted-args", // Warn if an argument list is modified to match the receiver.
    "-Xlint:by-name-right-associative", // By-name parameter of right associative operator.
    "-Xlint:constant", // Evaluation of a constant arithmetic expression results in an error.
    "-Xlint:delayedinit-select", // Selecting member of DelayedInit.
    "-Xlint:doc-detached", // A Scaladoc comment appears to be detached from its element.
    "-Xlint:inaccessible", // Warn about inaccessible types in method signatures.
    "-Xlint:infer-any", // Warn when a type argument is inferred to be `Any`.
    "-Xlint:missing-interpolator", // A string literal appears to be missing an interpolator id.
    "-Xlint:nullary-override", // Warn when non-nullary `def f()' overrides nullary `def f'.
    "-Xlint:nullary-unit", // Warn when nullary methods return Unit.
    "-Xlint:option-implicit", // Option.apply used implicit view.
    "-Xlint:package-object-classes", // Class or object defined in package object.
    "-Xlint:poly-implicit-overload", // Parameterized overloaded implicit methods are not visible as view bounds.
    "-Xlint:private-shadow", // A private field (or class parameter) shadows a superclass field.
    "-Xlint:stars-align", // Pattern sequence wildcard must align with sequence component.
    "-Xlint:type-parameter-shadow", // A local type parameter shadows a type already in scope.
    "-Xlint:unsound-match", // Pattern match may not be typesafe.
    "-Yno-adapted-args", // Do not adapt an argument list (either by inserting () or creating a tuple) to match the receiver.
    "-Ypartial-unification", // Enable partial unification in type constructor inference
    "-Ywarn-dead-code", // Warn when dead code is identified.
    "-Ywarn-extra-implicit", // Warn when more than one implicit parameter section is defined.
    "-Ywarn-inaccessible", // Warn about inaccessible types in method signatures.
    "-Ywarn-infer-any", // Warn when a type argument is inferred to be `Any`.
    "-Ywarn-nullary-override", // Warn when non-nullary `def f()' overrides nullary `def f'.
    "-Ywarn-nullary-unit", // Warn when nullary methods return Unit.
    "-Ywarn-numeric-widen", // Warn when numerics are widened.
    "-Ywarn-unused",
    "-Ywarn-unused-import",
    "-Ywarn-macros:after",
    "-Ywarn-unused:implicits", // Warn if an implicit parameter is unused.
    "-Ywarn-unused:imports", // Warn if an import selector is not referenced.
    "-Ywarn-unused:locals", // Warn if a local definition is unused.
    "-Ywarn-unused:params", // Warn if a value parameter is unused.
    "-Ywarn-unused:patvars", // Warn if a variable bound in a pattern is unused.
    "-Ywarn-unused:privates", // Warn if a private member is unused.
    "-Ywarn-value-discard" // Warn when non-Unit expression results are unused.
  )

}
