# 需修改的參數
## app id
APP_ID=sampleapp1
## app 名稱
APP_NAME=sampleapp1
## 平台版本
PLATFORM_VERSION=3.1.0
## dap 平台類別，前端就是 frontend, 後端就是 backend
DAP_TYPE=backend
## registry 位置
DOCKER_REGISTRY_URL=registry.digiwincloud.com


SHELL=/bin/bash

# make 指令
MAKE=make
# maven 指令
MAVEN=mvn
MAVEN_CLEAN=$(MAVEN) clean
MAVEN_PACKAGE=$(MAVEN) -f pom.xml package

#docker
DOCKER_CMD=docker
DOCKER_BUILD=$(DOCKER_CMD) build
DOCKER_RM_IMAGE=$(DOCKER_CMD) rmi
DOCKER_PUSH=$(DOCKER_CMD) push
DOCKER_IMAGE_REGISTRY=$(DOCKER_REGISTRY_URL)/$(APP_ID)/
DOCKER_IMAGE_NAME=$(APP_NAME)$(DAP_TYPE)-$(PLATFORM_VERSION)
DOCKER_FULL_IMAGE=$(DOCKER_IMAGE_REGISTRY)$(DOCKER_IMAGE_NAME):$(VERSION).$(shell cat $(SUB_VERSION_FILE))
#打包
VERSION:=$(shell cat VERSION)
#版本控制
SUB_VERSION_FILE=./version_control/BUILD

all: docker_ci
clean:
	$(MAVEN_CLEAN) -f pom.xml 
package:
	$(MAVEN_PACKAGE) -f pom.xml
docker_build:
	@echo "開始打包 Docker Image - $(DOCKER_FULL_IMAGE)"
	DOCKER_BUILDKIT=1 $(DOCKER_BUILD) -t $(DOCKER_FULL_IMAGE) .
docker_push:
	@echo "開始 push docker image - $(DOCKER_FULL_IMAGE)"
	$(DOCKER_PUSH) $(DOCKER_FULL_IMAGE)
docker_clean:
	$(DOCKER_RM_IMAGE) $(DOCKER_FULL_IMAGE)
docker_ci: vc docker_build docker_push docker_clean to_git
vc:
	@make -C version_control branch=$$branch
to_git:
	@make -C version_control branch=$$branch commit_record
	@make -C version_control branch=$$branch add_tag