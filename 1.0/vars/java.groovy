import com.henry.jenkins.pipeline.java.JavaUtils

class Global {
    static Object javautils = new JavaUtils()
}


def build() {
    Global.javautils.javaBuild()
}

def prepareDockerfileScript() {
    Global.javautils.prepareDockerfileScript()
}

def dockerImage() {
    Global.javautils.imageBuild()
}