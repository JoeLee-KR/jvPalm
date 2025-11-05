more checkFirststep.sh
#!/bin/bash
cd /svc/palm/sfAgent25
java -cp "./*:./libs/*" jvPalm.jvPalm
MSG=`date`
MSG="HELLO: $MSG"
logger $MSG

# more checkup
# java -cp "./*:./libs/*" jvPalm.checkSfJdbcConn
# java -cp "./*:./libs/*" jvPalm.checkMysqlJdbcConn
