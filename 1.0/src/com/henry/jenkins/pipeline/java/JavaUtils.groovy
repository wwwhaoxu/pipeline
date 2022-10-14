package com.henry.jenkins.pipeline.java

import com.henry.jenkins.pipeline.common.CommonUtils

class Global {
    static Object common = new CommonUtils()
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
            -t ${env.ECR_ADDR}/${params.SERVICE_NAME}:${BUILD_TAG} -f Dockerfile .  
        docker push ${env.ECR_ADDR}/${params.SERVICE_NAME}:${BUILD_TAG}
    """
}

def deploy() {

    dir("${WORKSPACE}/${SERVICE_NAME}"){
        sh """
            sed -i -E -e 's#(newTag:..).*(.\$)#\\1${BUILD_TAG}\\2#' ./base/kustomization.yaml
        
            cat ./base/kustomization.yaml
        """
        
        scirpt {
            withKubeConfig(credentialsId: params.NS, namespace: params.NS, serverUrl: Global.common.kusIP(params.NS)) {
            
                // sh 'kubectl apply -k ./overlay/${NS}'
                sh 'kubectl get ns'
                // script {   
                //    Global.common.delPod()    
                // }    
            }
        }
    }                
}

def checkStatus() {
    for(int count = 0; i < 60; count++) {
        sh 'sleep 5'
        Global.common.checkPodRun
    }
}
