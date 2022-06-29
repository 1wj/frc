#!/bin/sh
# dockerEnv.sh 參數
## 1. 掃描參數的資料夾
## 2. 掃描的檔案類型
## 3. 要排除掃描的檔案名稱
bash ./platform/bin/docker/dockerEnv.sh ./platform/bin sh dockerEnvReplace.sh dockerEnv.sh dockerRun.sh
sed -i s#@space@#" "#g ./platform/bin/run.sh
sed -i s#@space@#" "#g ./platform/bin/runEncrypt.sh
bash ./platform/bin/docker/dockerEnv.sh /jmx properties
bash ./platform/bin/docker/dockerEnv.sh ./platform/conf xml
bash ./platform/bin/docker/dockerEnv.sh ./platform/conf properties 
bash ./platform/bin/docker/dockerEnv.sh ./application properties
