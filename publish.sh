#!/bin/bash
cd /opt/ezadmin
rm -fr /opt/ezadmin/ezadmin-web/src/main/resources/application.yml
git pull

\cp -fr /opt/application.yml /opt/ezadmin/ezadmin-web/src/main/resources
/opt/apache-maven-3.9.6/bin/mvn clean package
ps -ef | grep ezadmin-web.jar | grep -v grep | awk '{print $2}' | xargs kill

nohup java -jar -Xms128M -Xmx512M  /opt/ezadmin/ezadmin-web/target/ezadmin-web.jar  > /opt/Log.log 2>&1 &


