export ZOOKEEPER_HOME=${ZOOKEEPER_HOME:-/usr/hdp/2.5.3.0-37/zookeeper}

java -cp  $ZOOKEEPER_HOME/zookeeper.jar:$ZOOKEEPER_HOME/lib/log4j-1.2.16.jar:$ZOOKEEPER_HOME/lib/slf4j-log4j12-1.6.1.jar:$ZOOKEEPER_HOME/lib/slf4j-api-1.6.1.jar org.apache.zookeeper.server.LogFormatter log.27c