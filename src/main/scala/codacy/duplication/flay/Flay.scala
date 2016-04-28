package codacy.duplication.flay

import codacy.dockerApi.api.{DuplicationClone, DuplicationCloneFile, DuplicationConfiguration}
import codacy.dockerApi.traits.IDuplicationImpl
import codacy.dockerApi.utils.CommandRunner
import java.io.File
import java.nio.file.Path
import play.api.libs.json.Json
import scala.util.{Failure, Properties, Try}

object Flay extends IDuplicationImpl {
  private val defaultMinTokenMatch = 10

  override def apply(rootPath: Path, config: DuplicationConfiguration): Try[List[DuplicationClone]] = {
    val rootDirectory = rootPath.toFile
    val result = CommandRunner.exec(getCommand(rootDirectory, config), Some(rootDirectory))

    result.right.map { output =>
      parseOutput(rootDirectory, output.stdout, config)
    } match {
      case Left(e) => Failure(e)
      case Right(reports) => reports
    }
  }

  private def getCommand(rootDirectory: File, config: DuplicationConfiguration): List[String] = {
    List("flay", s"-m ${minTokenMatch(config)}", "-d", "-f", rootDirectory.getCanonicalPath)
  }

  private def minTokenMatch(config: DuplicationConfiguration) = {
    config.params.get("minTokenMatch").flatMap(_.asOpt[Int]).getOrElse(defaultMinTokenMatch)
  }

  private def parseOutput(rootDirectory: File, output: Seq[String], config: DuplicationConfiguration): Try[List[DuplicationClone]] = {

    if (output.size != 1) {
      // output should be a single json line
      println(s"Warn: output should contain a single line, it contains ${output.size}")
    }

    Try {
      output.headOption.flatMap {
        jsonString =>
          Json.parse(jsonString).validate[FlayReport].asEither match {
            case Left(e) =>
              println("Error parsing flay output: $e")
              None
            case Right(report) =>
              val reports = reportToDuplication(report, config)
              Some(reports)
          }
      }
    }.map(_.getOrElse(List.empty))
  }

  private def reportToDuplication(report: FlayReport, config: DuplicationConfiguration): List[DuplicationClone] = {
    report.clones.map {
      clone =>
        val dupCloneFiles = clone.files.map {
          fileReport =>
            val startLine = fileReport.line
            val endLine = (fileReport.line + fileReport.contents.size) - 1
            val filePath = fileReport.filename.stripPrefix("/src/")
            DuplicationCloneFile(filePath, startLine, endLine)
        }
        val nrTokens = minTokenMatch(config)
        val nrLines = clone.files.headOption.map(_.contents.size).getOrElse(-1)
        val cloneLines = clone.files.map(_.contents.mkString(Properties.lineSeparator)).mkString(Properties.lineSeparator)

        DuplicationClone(cloneLines, nrTokens, nrLines, dupCloneFiles)
    }.toList
  }
}

