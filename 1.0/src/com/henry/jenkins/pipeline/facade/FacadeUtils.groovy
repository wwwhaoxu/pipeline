package com.henry.jenkins.pipeline.facade

import com.henry.jenkins.pipeline.common.CommonUtils

class Global {
    static Object common = new CommonUtils()
}

def info(message) {
    echo "INFO: ${message}"
}


def prepareDockerfileScript() {

    deploy = libraryResource 'com/henry/jenkins/pipeline/script/common_maven_deploy.sh'
    writeFile file: "./common_maven_deploy.sh",text: deploy
   
}

def javaBuild() {
    sh './common_maven_deploy.sh'
}



