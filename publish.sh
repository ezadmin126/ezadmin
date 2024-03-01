#!/bin/bash

cd /opt/ezadmin
git pull
\cp -fr /opt/application.yml /opt/ezadmin/ezadmin-web/src/main/resources
/opt/apache-maven-3.9.6/bin/mvn clean package
nohup java -jar -Xms128M -Xmx512M  /opt/ezadmin/ezadmin-web/target/ezadmin-web.jar  > /opt/Log.log 2>&1 &


