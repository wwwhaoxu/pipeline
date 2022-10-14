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

def deploy() {
    Global.javautils.checkStatus()
    Global.javautils.deploy()
    
}
def checkStatus() {
    Global.javautils.deploy()
}
