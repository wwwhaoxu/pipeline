import com.henry.jenkins.pipeline.HelmUtils

class Global {
    static Object helmUtils = new HelmUtils();
}

def yamlInitData() {
   
    def Map amap = [
    "replicaCount": params.SERVICE_REPLICACOUNT,
    "image": ["repository": params.APPLICATION_IMAGE_ECR + "/" + params.SERVICE_NAME, 
             "tag": params.ENV + "-" + params.SERVICE_VERSION],
    "START_SCRIPT": params.START_SCRIPT,
    "args": ["args": ["service_name": params.SERVICE_NAME, 
            "service_spa": "spring.profiles.active=" + params.SPRING_PROFILES_ACTIVE_ENV,
            "service_env": "env=" + params.ENV, "service_port": "server.port=" + params.PORT, 
            "service_workId": params.WORKID]],
    "java_opts": params.JAVA_OPTS,
    "self_opts": params.SELF_OPTS,
    "canal": ["disbled": params.CANAL_ENABLED],
    "volumes": ["pvc_name": params.PVC_NAME, "logs_dir": params.LOGS_DIR + params.SERVICE_NAME],
    "service": ["port": params.PORT, "port_tomcat": params.PORT_TOMCAT],
    "livenessProbe": ["enabled": params.PROBE_ENABLED],
    "readinessProbe": ["enabled": params.PROBE_ENABLED],
    "ingress": ["hosts": [['host': INGRESS_HOST, 
                "paths": [["path": "/", "backend": ["serviceName": SERVICE_NAME, "servicePort": PORT]]]]]],
    // "ingress": ["host": params.INGRESS_HOST],
    "resources": ["limits": ["cpu": params.RESOURCE_LIMIT_CPU, "memory": params.RESOURCE_LIMIT_MEM]],
    "worid": params.WORKID,
    "deploy_env": params.ENV,
    "nodeSelector": ["env": params.ENV]
    ];

    Global.helmUtils.yamlInit(amap)

}


def genYaml() {

    Global.helmUtils.genYaml()
    
}









