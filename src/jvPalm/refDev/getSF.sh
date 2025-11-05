#!/bin/bash
# main run-script by crontab
# */10 * * * * 	oasis 	/svc/palm/sfAgent25/getSF.sh
cd /svc/palm/sfAgent25
java -cp "./*:./libs/*" jvPalm.mvSFQH 0
MSG=`date`
MSG="getSF: $MSG"
logger $MSG
