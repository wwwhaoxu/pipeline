import com.henry.GlobalVars

def info(message) {
  echo "\033[32mINFO: ${message}\033[0m"
}

def build() {
  GlobalVars.build()
}
