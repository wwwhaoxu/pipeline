import com.henry.jenkins.pipeline.facade.FacadeUtils

class Global {
    static Object facadeutils = new FacadeUtils()
}


def build() {
    Global.facadeutils.javaBuild()
}

def prepareDockerfileScript() {
    Global.facadeutils.prepareDockerfileScript()
}


