# syntax=docker/dockerfile:experimental
## 使用 DOCKER_BUILDKIT=1 docker build -t 'imageName:tag' . 的方式進行打包
# 下載平台 apigateway（可討論是否由後端人員交付一個版本包含平台與maven的 base image）

ARG	    apiVersion=
ARG	    platformPath=/platform
ARG	    backendName=sampleapp1_backend
ARG	    packpath=/package

FROM          maven:3.5.3-jdk-8 AS base
ARG	          backendName
ARG	          platformPath
ENV           moduleRootPath=develop/module
ENV           moduleDependencyPath=${platformPath}/${moduleRootPath}/target/dependency
WORKDIR       ${platformPath}
COPY          ${backendName}/${moduleRootPath} ${moduleRootPath}
RUN           --mount=type=cache,target=/root/.m2 mvn -f ${moduleRootPath}/pom.xml clean package
RUN           tar -xvf ${moduleDependencyPath}/dwapiplatform-appbackend-*.war
RUN           mv app_backend ${backendName}

# 編譯
FROM        maven:3.5.3-jdk-8 AS builder
ARG	    apiVersion
ARG	    packpath
ARG	    backendName
ENV         build=/build modulePath=develop/module thirdParty=develop/DWThirdPartyLibrary
RUN         mkdir -p ${build}/thirdParty && mkdir -p ${packpath}/lib && mkdir -p ${packpath}/module
RUN	    mkdir -p ${packpath}/conf && mkdir -p ${packpath}/lang  && mkdir -p ${packpath}/lib
WORKDIR     ${build}
# config
COPY        ${backendName}/develop/conf/* ${packpath}/conf/
COPY        ${backendName}/develop/lang/* ${packpath}/lang/
COPY        ${backendName}/develop/lib/* ${packpath}/lib/
# thirdparty
# 目前sampleApp1沒有用到third party打包 先註解
#COPY        ${backendName}/${thirdParty} ${thirdParty}
#RUN         --mount=type=cache,target=/root/.m2 mvn -f ${thirdParty}/pom.xml package
#RUN         cp -r ${thirdParty}/target/dependency/* ${packpath}/lib

# module1
ENV         moduleName=Basic
ENV         pomVersion=3.1.0.1000-SNAPSHOT
COPY        ${backendName}/${modulePath}/${moduleName}  ${moduleName}
RUN         --mount=type=cache,target=/root/.m2 mvn -f ${moduleName}/pom.xml package
RUN         cp -r ${moduleName}/target/* ${packpath}/module
# module2
ENV         moduleName=dem
ENV         pomVersion=3.1.0.1000-SNAPSHOT
COPY        ${backendName}/${modulePath}/${moduleName}  ${moduleName}
RUN         --mount=type=cache,target=/root/.m2 mvn -f ${moduleName}/pom.xml package
RUN         cp -r ${moduleName}/target/* ${packpath}/module
# module3
ENV         moduleName=DEMO_DAP_CURRENT
ENV         pomVersion=3.1.0.1000-SNAPSHOT
COPY        ${backendName}/${modulePath}/${moduleName}  ${moduleName}
RUN         --mount=type=cache,target=/root/.m2 mvn -f ${moduleName}/pom.xml package
RUN         cp -r ${moduleName}/target/* ${packpath}/module

# 打包
FROM        registry.digiwincloud.com/dwsidecar/dwsidecar-1.0.0.0:1.0.0.24
ARG	    platformPath
ARG	    backendName
ARG	    packpath
COPY        --from=base ${platformPath}/${backendName} /${backendName}
WORKDIR     /${backendName}
COPY        --from=builder ${packpath}/ ./application/

RUN         chmod +x ./platform/bin/run.sh \
            && chmod +x ./platform/bin/stop.sh \
            && chmod +x ./platform/bin/docker/dockerEnv.sh \
            && chmod +x ./platform/bin/docker/dockerEnvReplace.sh \
            && chmod +x ./platform/bin/docker/dockerRun.sh
EXPOSE      22620
ENTRYPOINT  ["/sampleapp1_backend/platform/bin/docker/dockerRun.sh"]
