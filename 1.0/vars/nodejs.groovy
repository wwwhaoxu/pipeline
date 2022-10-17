import com.henry.jenkins.pipeline.nodejs.NodejsUtils

class Global {
    static Object nodejsutils = new NodejsUtils()
}


def build() {
    Global.nodejsutils.javaBuild()
}

def prepareDockerfileScript() {
    Global.nodejsutils.prepareDockerfileScript()
}

def dockerImage() {
    Global.nodejsutils.imageBuild()
}

def deploy() {
    Global.nodejsutils.deploy()
    
}
def checkStatus() {
    Global.nodejsutils.checkStatus()
}
