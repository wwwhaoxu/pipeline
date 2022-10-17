function PrintTime() {
    text=$1
    echo -e "[$(date +'%F %T')] $text"
}

function MvnFacadeDeploy() {
        is_common_project='False'
        mvn_build_command="mvn clean deploy -U -e -Dmaven.test.skip=true -Dmaven.compile.fork=true"
        common_projects=( \
            "upex-common" \
            "new-upex-common" \
            "old-upex-common" \
            "upex-common-start-parent" \
            "exchange-framework" \
            "trigger-parent" \
            "upex-trigger-mapper" \
            "mc-common" \
            "upex-data-dto" \
            "upex-data-facade" \
            "upex-common-quant" \
            "upex-parent" \
            "base-data" \
            "quant-xxl-job" \
            "mc-parent" \
            "upex-xxl-job" \
            "apollo-v1" \
            "apollo-v2" \
            "huobi-client" \
            "upex-boss-commons" \
            "zil-sdk" \
            "upex-social-facade" \
        )
        for project in ${common_projects[*]};do
            if [[ "${project}" == "${BK_CI_PIPELINE_NAME}" ]];then
		PrintTime "${BK_CI_PIPELINE_NAME}"
                is_common_project='True'
                break
            fi
        done
	PrintTime "is_common_project=$is_common_project"
        #grep -r -i 'release' * && PrintTime "项目目录下 pom.xml带release关键字，走线上发布吧" && exit 1
        if [[ "$is_common_project" == 'True' ]];then
            PrintTime "开始构建公共服务项目"
            PrintTime "maven 打包命令: $mvn_build_command"
            $mvn_build_command || ( PrintTime "maven命令执行失败!" && exit 1 )
        else
            PrintTime "开始构建"
            mvn_build_command="mvn clean package deploy -pl ."
            for keyword in -dto -facade -feign -feign1 -feign2;do
                dir=$(find . -maxdepth 1 -name "*${keyword}" | xargs | sed 's/ /,/')
		PrintTime "dir=${dir}"
                if [[ "$dir" != '' ]];then
                    mvn_build_command+=",${dir//\.\//}"
                fi
            done
            PrintTime "maven 打包命令: $mvn_build_command"
            $mvn_build_command || ( PrintTime "maven命令执行失败!" && exit 1 )
        fi
}