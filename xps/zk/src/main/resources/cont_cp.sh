for i in `seq 1 100`;
do
 mkdir ~/copies/$(date +'%H:%M:%S:%N') && cp -r /grid/1/hadoop/zookeeper/version-2/snapshot* $_
done