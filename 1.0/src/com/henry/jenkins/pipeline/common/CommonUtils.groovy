package com.henry.jenkins.pipeline.common


// def replace(String path, String a, String b) {
   
//     File testFile = new File("/Users/wanghaoxu", 'Dockerfile').getCanonicalPath()
//     String after = testFile.getText("utf-8").replaceAll(a, b)
//     println after
//     testFile.write(after)

// }
@NonCPS
static def replace(file, oldText, newText) {
    String text = file.text.replaceAll(oldText, newText)
    file.withPrintWriter { printWriter ->
        printWriter.print(text)
    }
}


def info(message) {
  echo "INFO: ${message}"
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

def String kusIP(String ns) {
   def Map nstoip = ["online": "https://172.18.28.174:6443"]
   return nstoip[ns]
}


def checkPodRun(Sting gvr) {
    def result = sh(script: "kubectl get " + gvr + "/${SERVICE_NAME} | awk 'NR >1 {print \$2}", returnStdout: true)
    // 1/1
    sh """
        runCount=${result:0:1}
        totalCount=${result:2:1}
        if [ $runCount == $totalCount ];then
            echo "succesful"
            exit 0
        fi
    """ 
}





