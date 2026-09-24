package codacy.duplication.flay

import com.codacy.plugins.api.Source
import com.codacy.plugins.api.duplication.{DuplicationClone, DuplicationCloneFile}
import org.specs2.mutable.Specification

import scala.util.Success

class FlaySpec extends Specification {

  val resourceDirectory =
    new java.io.File(getClass.getClassLoader.getResource(".").toURI)

  "Flay" should {
    "get clones" in {
      val dir = resourceDirectory + "/analysis/duplication/ruby"

      val expectedClones = List(
        DuplicationClone(
          """msg = "sexp_to_#{File.extname(node.file).sub(/./, "")}"
                           |self.respond_to?(msg) ? (self.send(msg, node)) : (sexp_to_rb(node))
                           |msg = "sexp_to_#{File.extname(s.file).sub(/./, "")}"
                           |self.respond_to?(msg) ? (self.send(msg, s)) : (sexp_to_rb(s))""".stripMargin,
          1,
          2,
          List(DuplicationCloneFile("flay.rb", 532, 533), DuplicationCloneFile("flay.rb", 588, 589))),
        DuplicationClone(
          """opts.on("-m", "--mass MASS", Integer, "Sets mass threshold (default = #{options[:mass]})") do |m|
            |  options[:mass] = m.to_i
            |end
            |opts.on("-t", "--timeout TIME", Integer, "Set the timeout. (default = #{options[:timeout]})") do |t|
            |  options[:timeout] = t.to_i
            |end""".stripMargin,
          1,
          3,
          List(DuplicationCloneFile("flay.rb", 85, 87), DuplicationCloneFile("flay.rb", 110, 112))))
        .sortBy(_.cloneLines)

      val clonesTry = Flay(Source.Directory(dir), None, Map.empty)

      clonesTry should beLike {
        case Success(clones) =>
          clones.length should beEqualTo(expectedClones.length)
          clones.sortBy(_.cloneLines) should beEqualTo(expectedClones)
      }
    }
  }

}
