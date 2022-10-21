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
    
    def result = sh(script: "kubectl get pod  |grep '0/1' |grep ${params.SERVICE_NAME}  | awk '{print \$1}'", returnStdout: true)

    if (result.length() != 0) {
        info "start delte pod..."
        podList = result.split('\n')
        for(pod in podList) {
            sh "kubectl delete pod/${pod}"
        }
    }
    info "not need to delete pod"       
    
}

def String kusIP(String ns) {

   def Map nstoip = ["online": "https://172.18.28.174:6443"]
   return nstoip[ns]

}


def boolean checkPodRun(String gvr) {
   
   def result = sh(script: "kubectl get " + gvr + "/${params.SERVICE_NAME} | awk 'NR >1 {print \$2}'", returnStdout: true)
    // 1/1
    result = result.split("/")
    runCount = result[0]
    totalCount = result[1]
    return runCount == totalCount
   
}

def trivyScan() {

    // sh 'curl -sfL https://raw.githubusercontent.com/aquasecurity/trivy/main/contrib/html.tpl -o html.tpl'

    template = libraryResource 'com/henry/jenkins/pipeline/templates/html.tpl'
    writeFile file: "./html.tpl",text: template

    // Scan all vuln levels
    sh 'trivy -v'
    sh 'trivy image --severity HIGH,CRITICAL \
    --format template --template @./html.tpl \
    -o report.html \
    aliyun-acr-registry-vpc.cn-hongkong.cr.aliyuncs.com/base_image/node:v16.13.1-amazonlinux-2'

    publishHTML target : [
        allowMissing: true,
        alwaysLinkToLastBuild: true,
        keepAll: true,
        reportDir: './',
        reportFiles: 'report.html',
        reportName: 'Trivy Scan',
        reportTitles: 'Trivy Scan'
    ]

}





