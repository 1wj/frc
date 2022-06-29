#!/bin/sh
cd ..
cd ..
#Backend/
WORKDIR=`pwd`
export WORKDIR
CLASSPATH=.:$WORKDIR:$WORKDIR/platform/lib/*:$WORKDIR/platform/conf:$WORKDIR/platform/lang
export CLASSPATH

if test -z "$pinpoint_appname";
then
  java -cp $CLASSPATH @jvmArgs@ com.digiwin.app.common.launcher.DWXClassLauncher --xclass.mainclass=com.digiwin.gateway.DWApiGatewayApplication --xclass.password=@classPassword@
else
  export AGENT_PATH=/agent_pinpoint/pinpoint-bootstrap.jar
  export AGENT_NAME=$pinpoint_appname
  export AGENT_ID=$pinpoint_id
  export AGENT_OPTS="-javaagent:$AGENT_PATH -Dpinpoint.agentId=$AGENT_ID -Dpinpoint.applicationName=$AGENT_NAME"
  java $AGENT_OPTS -cp $CLASSPATH @jvmArgs@ com.digiwin.app.common.launcher.DWXClassLauncher --xclass.mainclass=com.digiwin.gateway.DWApiGatewayApplication --xclass.password=@classPassword@
fi
