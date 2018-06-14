package codacy.duplication.flay

import play.api.libs.functional.syntax._
import play.api.libs.json.{Json, OFormat, _}

final case class FileReport(filename: String, line: Int, contents: Seq[String])

object FileReport {
  implicit val fmt: OFormat[FileReport] = Json.format[FileReport]
}

final case class Clone(mass: Int, name: String, files: Seq[FileReport])

object Clone {
  implicit val fmt: OFormat[Clone] = Json.format[Clone]
}

final case class FlayReport(total: Int, clones: List[Clone])

object FlayReport {
  implicit val reportFmt: Reads[FlayReport] = (
    (JsPath \ "total").read[Int] and
      (JsPath \ "clones").readNullable[List[Clone]].map(_.getOrElse(List.empty))
  )(FlayReport.apply _)
}
