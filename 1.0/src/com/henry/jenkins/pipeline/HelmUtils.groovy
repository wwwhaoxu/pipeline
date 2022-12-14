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

        if [ ${params.GVR} == "deployment" ]; then
            sed -i -E  \
                 -e 's@(.*)statefulset(.*)@#\\1statefulset\\2@' \
                 -e 's@(.*)service-headless(.*)@#\\1service-headless\\2@' \
            ./${SERVICE_NAME}/base/kustomization.yaml
        else 
            sed -i -E  \
                 -e 's@(.*)deployment(.*)@#\\1deployment\\2@' \
                 -e 's@(.*)service.yaml@#\\1service.yaml@'  \
            ./${SERVICE_NAME}/base/kustomization.yaml
        fi
    """
}




