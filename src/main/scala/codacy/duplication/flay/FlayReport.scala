package codacy.duplication.flay

import play.api.libs.functional.syntax._
import play.api.libs.json.{Json, _}

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
  implicit val reportFmt: Reads[FlayReport] = (
    (JsPath \ "total").read[Int] and
      (JsPath \ "clones").readNullable[Seq[Clone]].map(_.getOrElse(Seq.empty))
    ) (FlayReport.apply _)
}
