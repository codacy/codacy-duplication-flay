package codacy.duplication.flay

import play.api.libs.json.Json

case class FileReport(filename: String, line: Int, contents: Seq[String])

object FileReport {
  implicit val fmt = Json.format[FileReport]
}

case class Clone(mass: Int, name: String, files: Seq[FileReport])

object Clone {
  implicit val fmt = Json.format[Clone]
}

case class FlayReport(total: Int, clones: Seq[Clone])

object FlayReport {
  implicit val fmt = Json.format[FlayReport]
}