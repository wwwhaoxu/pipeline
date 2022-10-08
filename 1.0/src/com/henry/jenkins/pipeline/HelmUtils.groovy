package com.henry.jenkins.pipeline


def yamlInit(Map data) {    
    dir(env.HELM_WORKSPACE) {
        sh "rm -f override.yaml"
        writeYaml file: "override.yaml", data: data
    }
}

def genYaml() {

    sh """
        cd ${env.WORKSPACE}

        [ ! -d ${SERVICE_NAME} ] || rm -r ${SERVICE_NAME}
        
        cp -rf kusTemplate  ${SERVICE_NAME}

        helm template ${SERVICE_NAME} ${HELM_WORKSPACE} -f ${HELM_WORKSPACE}/override.yaml --output-dir ${SERVICE_NAME}
        
        sed -i -E 's#(name..).*#\\1${APPLICATION_IMAGE_ECR}/${SERVICE_NAME}#' ./${SERVICE_NAME}/base/kustomization.yaml
    """
}




