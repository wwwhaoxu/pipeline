class GlobalVars {
  static String foo = "bar"
}

def build() {
  println GlobalVars.foo
}
