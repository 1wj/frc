cd ..
cd ..
SET CLASSPATH=./;./platform/lib/*;./platform/conf;./platform/lang
SET PATH=%PATH%;C:\Program Files\Java\jdk1.8.0_131\bin
java -cp %CLASSPATH% com.digiwin.app.common.launcher.DWXClassLauncher --xclass.mainclass=com.digiwin.gateway.DWApiGatewayApplication --xclass.password=@classPassword@