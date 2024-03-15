#!/bin/bash


yum install -y  java-1.8.0-openjdk-devel

cd /opt

tar -zxvf apache-maven-3.9.6-bin.tar.gz


yum install -y git

git clone https://github.com/ezadmin126/ezadmin.git

cd ezadmin 

rm -fr /opt/ezadmin/ezadmin-web/src/main/resources/application.yml

\cp -fr /opt/application.yml /opt/ezadmin/ezadmin-web/src/main/resources

/opt/apache-maven-3.9.6/bin/mvn clean package  

nohup java -jar -Xms128M -Xmx512M  /opt/ezadmin/ezadmin-web/target/erp.jar  > /opt/Log.log 2>&1 &


