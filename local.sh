#!/bin/bash
mvn -Dmaven.test.skip=true package
# java -jar -jar target/mailclena-0.0.1-SNAPSHOT-executable.jar -h=pop3.goneo.de -u=nursenden@ottlinger.de -p="SecurityByObscurity!2018"
# java -jar -jar target/mailclena-0.0.1-SNAPSHOT-executable.jar -h=pop3.goneo.de -u=nursenden@kaishinkan.de -p="SecurityByObscurity!2018"
java -jar -jar target/mailclena-0.0.1-SNAPSHOT-executable.jar -h=pop3.goneo.de -u=nursenden@hugo-hirsch.de -p="SecurityByObscurity!2018"
