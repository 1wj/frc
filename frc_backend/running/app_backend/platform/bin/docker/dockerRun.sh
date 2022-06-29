#!/bin/sh
bash ./platform/bin/docker/dockerEnvReplace.sh
bash /setup/setup_time.sh
bash /jmx/start_monitor_jmx.sh
cd ./platform/bin/
sh run.sh
