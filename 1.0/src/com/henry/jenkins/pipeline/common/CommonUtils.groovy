package com.henry.jenkins.pipeline.common

def replace(String path, String a, String b) {
    
    File testFile = new File(path, 'Dockerfile').getCanonicalPath()
    String after = testFile.getText("utf-8").replaceAll(a, b)
    println after
    testFile.write(after)

}

def delPod() {
    
    def result = sh(script: "kubectl get pod  |grep '0/1' |grep ${SERVICE_NAME}  | awk '{print \$1}'", returnStdout: true)
                
    def podList = result.split('\n')
    
    if (podList.size() > 0) {
        println(podList.size())
        for(pod in podList) {
            sh "kubectl delete pod/${pod}"
        }
    }
}


def Map nstoip = ["online": "https://172.18.28.174:6443"]




