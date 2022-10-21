package com.henry.jenkins.pipeline.java

import com.henry.jenkins.pipeline.common.CommonUtils

class Global {
    static Object common = new CommonUtils()
}

def info(message) {
    echo "INFO: ${message}"
}

// 准备Dockerfile和启动脚步
// @NonCPS
def prepareDockerfileScript() {

    javaDockerfile = libraryResource 'com/henry/jenkins/pipeline/dockerfile/javaDockerfile'
    writeFile file: "./Dockerfile",text: javaDockerfile

    sh """
        cp -rf ${env.WORKSPACE}/entrypoint/entrypoint_spring*.sh ./
        cp -rf ${env.WORKSPACE}/upload_files/iast_agent.jar ./

    """
    
    // def path = sh(script: 'pwd', returnStdout: true).trim()
    // Global.common.replace("BASE_IMAGE", "aaa")
    // Global.common.replace("JAR_NAME", "bbb")
    // Global.common.replace(new File("/Users/wanghaoxu/Dockerfile"), "BASE_IMAGE", "aaa")    
    // Global.common.info "hello world"
}

def javaBuild() {
    sh 'mvn clean package -U -Dmaven.test.skip'
}

def imageBuild() {
    sh """
        docker build \
            --build-arg BASE_IMAGE=${params.BASE_IMAGE} \
            --build-arg SERVICE_NAME=${params.SERVICE_NAME} \
            --build-arg JAR_NAME=${params.JAR_NAME} \
            -t ${env.ECR_ADDR}/${params.SERVICE_NAME}:${BUILD_TAG} -f Dockerfile .  
        docker push ${env.ECR_ADDR}/${params.SERVICE_NAME}:${BUILD_TAG}
    """
}

def deploy() {
    
//     因为CPS 限制必须先吊用Global.common中的任意一个方法，后面的 Global.common.delPod()、Global.common.checkPodRun(params.GVR) 才
//     能执行成功
    
    Global.common.info "starting deploy ..."


    dir("${WORKSPACE}/${SERVICE_NAME}"){
        sh """
            sed -i -E -e 's#(newTag:..).*(.\$)#\\1${BUILD_TAG}\\2#' ./base/kustomization.yaml
        
            cat ./base/kustomization.yaml
        """
        
        withKubeConfig(credentialsId: params.NS, namespace: params.NS, serverUrl: Global.common.kusIP(params.NS)) {
            
            sh "kubectl apply -k ./overlay/${params.NS}"
            // sh 'kubectl get ns'

            info "start delete pod ..."
            Global.common.delPod()


            // Global.common.info "starting check status ..."
            info "starting check status ..."

            for(int count = 0; count < 60; count ++) {
                
                sleep(5)
                if(Global.common.checkPodRun(params.GVR)) {
                    info "恭喜你successful";
                    break
                }
                info "Don't worry, we could try agin!"
            }
            info "try 60 times, Sorry failed !!!"
        }
    }    
}

def checkStatus() {
    Global.common.info "hello world"
    for(int count = 0; count < 60; count++) {
        sh 'sleep 5'
//         Global.common.checkPodRun("sts")
        Global.common.info("hello world")
    }
}


def imageScan() {
    info "start docker image scan..."
    Global.common.trivyScan()
}

