package com.henry.jenkins.pipeline.nodejs

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

    javaDockerfile = libraryResource 'com/henry/jenkins/pipeline/dockerfile/nodejsDockerfile'
    writeFile file: "./Dockerfile",text: nodejsDockerfile

}

def nodejsBuild() {
    sh """
        if [ -d "node_modules" ]; then
            mv  node_modules{,.$(date +%F%T)}
        fi  

        if [ -e "package-lock.json" ]; then
            mv  package-lock.json{,.$(date +%F%T)}
        fi

        cd ${params.SERVICE_NAME/-/_}
        ${env.NODE_PATH}/pnpm install --shamefully-hoist
        ${env.NODE_PATH}/pnpm run build:${params.NS}
    """
}

def imageBuild() {
    sh """
        docker build \
            --build-arg BASE_IMAGE=${params.BASE_IMAGE} \
            --build-arg SERVICE_NAME=${params.SERVICE_NAME} \
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

            for(int count = 0; count < 6; count ++) {
                
                sleep(5)
                if(Global.common.checkPodRun(params.GVR)) {
                    info "恭喜你successful";
                    break
                }
                info "Don't worry, we could try agin!"
            }
            info "try 6 times, Sorry failed !!!"
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

