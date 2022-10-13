package com.henry.jenkins.pipeline.java

import com.henry.jenkins.pipeline.common.CommonUtils

class Global {
    static Object common = new CommonUtils()
}

// 准备Dockerfile和启动脚步
def prepareDockerfileScript() {
    sh """
        cp -rf ${env.WORKSPACE}/dockerfile_template/spring.Dockerfile ./Dockerfile
        cp -rf ${env.WORKSPACE}/entrypoint/entrypoint_spring*.sh ./
        cp -rf ${env.WORKSPACE}/upload_files/iast_agent.jar ./

        sed -i -e 's/{SERVICE_NAME}/${params.SERVICE_NAME}/g' Dockerfile
    """
    
    def path = sh(script: 'pwd', returnStdout: true).trim()
    common.replace(path, "BASE_IMAGE", BASE_IMAGE)
    common.replace(path, "JAR_NAME", JAR_NAME)
        
}

def javaBuild() {
    sh 'mvn clean package -U -Dmaven.test.skip'
}

def imageBuild() {
    sh """
        docker build -t ${env.ECR_ADDR}/${params.SERVICE_NAME}:${BUILD_TAG} -f Dockerfile .  
        docker push ${env.ECR_ADDR}/${params.SERVICE_NAME}:${BUILD_TAG}
    """
}

def deploy() {

    dir("${WORKSPACE}/${SERVICE_NAME}"){
        sh "sed -i -E -e 's#(newTag:..).*(.\$)#\\1${BUILD_TAG}\\2#' \
        ./base/kustomization.yaml"
        
        sh "cat ./base/kustomization.yaml"
        
        withKubeConfig(credentialsId: 'ack-online', namespace: '${NS}', serverUrl: common.nstoip[params.NS]) {
            
            sh 'kubectl apply -k ./overlay/${NS}'
            
            script {   
               common.delPod()    
            }    
        }
    }                
}

def checkStatus() {
    for(int count = 0; i < 60; count++) {
        sh 'sleep 5'
        common.checkPodRun
    }
}
