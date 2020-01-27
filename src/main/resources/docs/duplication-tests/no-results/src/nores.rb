
def code_index
{
    :block  => 0,    # s(:block,                   *code)
    :class  => 2,    # s(:class,      name, super, *code)
    :module => 1,    # s(:module,     name,        *code)
    :defn   => 2,    # s(:defn,       name, args,  *code)
    :defs   => 3,    # s(:defs, recv, name, args,  *code)
    :iter   => 2,    # s(:iter, recv,       args,  *code)
}[self.sexp_type]
end
