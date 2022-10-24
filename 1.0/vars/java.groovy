import com.henry.jenkins.pipeline.java.JavaUtils

env.base_image="aaa"
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
    Global.javautils.deploy()
    
}
def checkStatus() {
    Global.javautils.checkStatus()
}

def imageScan() {
    Global.javautils.imageScan()
}
