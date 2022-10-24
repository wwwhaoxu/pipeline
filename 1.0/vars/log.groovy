import com.henry.GlobalVars

def info(message) {
  echo GlobalVars.foo
  echo '\033[32mINFO: ${message}\033[0m'
}
