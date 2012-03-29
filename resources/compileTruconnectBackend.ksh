#!/usr/bin/ksh
. /apps/webadmin/.profile

###############################################################################
##
##	Compile TruConnectBackend##
##    -This script is designed compile the java code on the hosting server
###############################################################################

PROJECT=$HOME/tscp/mvne/truconnect

########################
## REQUIRED LIBRARIES ##
########################
CLASSPATH=$CLASSPATH:$PROJECT/bin/
CLASSPATH=$CLASSPATH:$PROJECT/lib/ojdbc14.jar
CLASSPATH=$CLASSPATH:$PROJECT/lib/mail.jar
CLASSPATH=$CLASSPATH:$PROJECT/lib/jta-1.1.jar
CLASSPATH=$CLASSPATH:$PROJECT/lib/javassist-3.12.0.GA.jar
CLASSPATH=$CLASSPATH:$PROJECT/lib/hibernate3.jar
CLASSPATH=$CLASSPATH:$PROJECT/lib/dom4j-1.6.1.jar
CLASSPATH=$CLASSPATH:$PROJECT/lib/commons-collections-3.1.jar
CLASSPATH=$CLASSPATH:$PROJECT/lib/antlr-2.7.6.jar
CLASSPATH=$CLASSPATH:$HOME/SUNWappserver/lib/activation.jar

#######################
## LOGGING LIBRARIES ##
#######################
CLASSPATH=$CLASSPATH:$PROJECT/lib/slf4j-api-1.6.1.jar
CLASSPATH=$CLASSPATH:$PROJECT/lib/logback-classic-0.9.30.jar
CLASSPATH=$CLASSPATH:$PROJECT/lib/logback-core-0.9.30.jar

########################
## TSCPMVNE LIBRARIES ##
########################
CLASSPATH=$CLASSPATH:$PROJECT/lib/TSCPMVNE-API-2.0.jar

echo 'Classpath:'
echo $CLASSPATH

#javac -classpath $CLASSPATH -d bin $1
javac -cp $CLASSPATH -d ../bin com/tc/bu/TruConnectBackend.java