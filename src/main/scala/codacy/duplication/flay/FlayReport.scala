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


// TODO REMOVE
object X {
  val testString ="""{"total":114,"clones":[{"prefix":"1","match":"Similar","mass":42,"name":"block","files":[{"filename":"lib/flay.rb","line":550,"contents":["msg = \"sexp_to_#{File.extname(node.file).sub(/./, \"\")}\"\nself.respond_to?(msg) ? (self.send(msg, node)) : (sexp_to_rb(node))\n"]},{"filename":"lib/flay.rb","line":609,"contents":["msg = \"sexp_to_#{File.extname(s.file).sub(/./, \"\")}\"\nself.respond_to?(msg) ? (self.send(msg, s)) : (sexp_to_rb(s))\n"]}]},{"prefix":"2","match":"Similar","mass":36,"name":"iter","files":[{"filename":"lib/flay.rb","line":76,"contents":["opts.on(\"-m\", \"--mass MASS\", Integer, \"Sets mass threshold (default = #{options[:mass]})\") do |m|\n  options[:mass] = m.to_i\nend"]},{"filename":"lib/flay.rb","line":101,"contents":["opts.on(\"-t\", \"--timeout TIME\", Integer, \"Set the timeout. (default = #{options[:timeout]})\") do |t|\n  options[:timeout] = t.to_i\nend"]}]},{"prefix":"3","match":"Similar","mass":36,"name":"defn","files":[{"filename":"lib/flay_erb.rb","line":28,"contents":["def add_expr_literal(src, code)\n  if code.=~(BLOCK_EXPR) then\n    ((src << \"@output_buffer.append= \") << code)\n  else\n    (((src << \"@output_buffer.append=(\") << code) << \");\")\n  end\nend"]},{"filename":"lib/flay_erb.rb","line":36,"contents":["def add_expr_escaped(src, code)\n  if code.=~(BLOCK_EXPR) then\n    ((src << \"@output_buffer.safe_append= \") << code)\n  else\n    (((src << \"@output_buffer.safe_append=(\") << code) << \");\")\n  end\nend"]}]}]} """
  Json.parse(testString).asOpt[FlayReport]
}
