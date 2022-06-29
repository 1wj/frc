#!/bin/sh
cd ..
cd ..
#Backend/
WORKDIR=`pwd`
export WORKDIR

CLASSPATH=.:$WORKDIR:$WORKDIR/platform/lib/*:$WORKDIR/platform/conf:$WORKDIR/platform/lang
export CLASSPATH

sh /dwsidecar/sidecar/bin/docker/dockerRun.sh &

if test -z "$pinpoint_appname";
then
  java -cp $CLASSPATH @jvmArgs@ com.digiwin.gateway.DWApiGatewayApplication
else
  export AGENT_PATH=/agent_pinpoint/pinpoint-bootstrap.jar
  export AGENT_NAME=$pinpoint_appname
  export AGENT_ID=$pinpoint_id
  export AGENT_OPTS="-javaagent:$AGENT_PATH -Dpinpoint.agentId=$AGENT_ID -Dpinpoint.applicationName=$AGENT_NAME"
  java $AGENT_OPTS -cp $CLASSPATH @jvmArgs@ com.digiwin.gateway.DWApiGatewayApplication
fi
