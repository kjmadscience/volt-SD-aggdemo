#!/bin/sh

cd ../jars 

USERCOUNT=100000
KAF=0
DURATION=600
VDBHOSTS=`cat $HOME/.vdbhostnames`
RUNSTART=`date '+%y%m%d_%H%M'`
KAFKAPORT=9092

for j in 10 20 30 40  50 60 70 80 90 100
do 
	echo $j
	echo "delete from cdr_dupcheck;" | sqlcmd --servers=${VDBHOSTS}
	echo "upsert into mediation_parameters (parameter_name ,parameter_value) VALUES ('AGG_QTYCOUNT',1);"| sqlcmd --servers=${VDBHOSTS}
	sleep 6
	for i in 0 
	do
       		nohup java -jar voltdb-aggdemo-client.jar ${VDBHOSTS} ${USERCOUNT} $j ${DURATION} 1000 1000 1000 10000 $i ${KAF} ${KAFKAPORT} 10000 > ${RUNSTART}_${j}_${i}.out &
		sleep 1
	done
	wait
	sleep 6
done

