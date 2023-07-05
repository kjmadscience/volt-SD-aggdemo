#!/bin/sh

cd ../jars 

USERCOUNT=100000
KAF=1
DURATION=3600
VDBHOSTS=`cat $HOME/.vdbhostnames`
RUNSTART=`date '+%y%m%d_%H%M'`
KAFKAPORT=9092
BUSY_CACHE=0

for j in 2 5 10 20 30 40  50 60 70 80 90 100
do 
	echo $j
	echo "delete from cdr_dupcheck;" | sqlcmd --servers=${VDBHOSTS}
	sleep 60
	for i in 0 100000 200000 300000 400000 500000 600000 700000 800000 900000
	do
       		nohup java -jar voltdb-aggdemo-client.jar ${VDBHOSTS} ${USERCOUNT} $j ${DURATION} 1000 1000 1000 10000 $i ${KAF} ${KAFKAPORT} ${BUSY_CACHE} > ${RUNSTART}_${j}_${i}.out &
		sleep 1
	done
	wait
	sleep 60
done

