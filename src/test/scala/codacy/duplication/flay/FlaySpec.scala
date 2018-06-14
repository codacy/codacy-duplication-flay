package codacy.duplication.flay

import codacy.docker.api.Source
import codacy.docker.api.duplication.{DuplicationClone, DuplicationCloneFile}
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
          10,
          2,
          List(DuplicationCloneFile("flay.rb", 548, 549), DuplicationCloneFile("flay.rb", 607, 608))),
        DuplicationClone(
          """opts.on("-m", "--mass MASS", Integer, "Sets mass threshold (default = #{options[:mass]})") do |m|
            |  options[:mass] = m.to_i
            |end
            |opts.on("-t", "--timeout TIME", Integer, "Set the timeout. (default = #{options[:timeout]})") do |t|
            |  options[:timeout] = t.to_i
            |end""".stripMargin,
          10,
          3,
          List(DuplicationCloneFile("flay.rb", 76, 78), DuplicationCloneFile("flay.rb", 101, 103))),
        DuplicationClone(
          """def add_expr_literal(src, code)
                           |  if code.=~(BLOCK_EXPR) then
                           |    ((src << "@output_buffer.append= ") << code)
                           |  else
                           |    (((src << "@output_buffer.append=(") << code) << ");")
                           |  end
                           |end
                           |def add_expr_escaped(src, code)
                           |  if code.=~(BLOCK_EXPR) then
                           |    ((src << "@output_buffer.safe_append= ") << code)
                           |  else
                           |    (((src << "@output_buffer.safe_append=(") << code) << ");")
                           |  end
                           |end""".stripMargin,
          10,
          7,
          List(DuplicationCloneFile("flay_erb.rb", 28, 34), DuplicationCloneFile("flay_erb.rb", 36, 42))))
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
