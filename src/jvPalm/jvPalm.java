package jvPalm;

public class jvPalm {
    String[] msgs = new String[] {
            "This JAR file to dump data from (remote)SF to (local)MYSQL.",
            "General command pattern is: ",
            "  $ java -cp \"LIB_PATH/*:JAR_PATH/*\" YOUR_MAIN_METHOD ARGS",
            "  $ java -cp \"/svc/palm/sfAgent/drivers/*:/svc/palm/sfAgent/bin/*\" jvPalm.checkMysqlJdbcConn 0",
            "",
            "included methods are bellow:",
            "  >> jvPalm.checkMysqlJdbcConn [0..1]",
            "     0: mysql://palmmysqldb:3306, 1: mariadb://palmmysqldb:3306",
            "     2: mysql://palmmysqldb:9306, 3: mariadb://palmmysqldb:9306",
            "  >> jvPalm.checkSfJdbcConn [0..3]",
            "     0: Internet atixoaj, 1: Internet jx75304",
            "     2: Private atixoaj,  3: Private jx75304",
            "  >> jvPalm.mvSFM_QueryHistory [0..1] (minutes | Date Hour)",
            "     no arg: this help message" ,
            "     1 arg: now-15 min getting" ,
            "       1st arg: 0:SF-Internet-3306, 1:SF-Private-3306, 2:SF-Internet-9306, 3:SF-Private-9306",
            "     2 args: now-X min getting",
            "       1st arg + 2nd arg: -X is minute range, w/overDUP",
            "     3 args: at target day and with hourly",
            "       1st arg + 2nd arg: target yyyy-mm-dd, 3rd arg: hourly(0..23) & 24 is hole day, w/overDUP",
            "..."
    };

    public void printout(String[] mmm){

        for(String aString : mmm) {
            System.out.println(aString);
        }
    }

    public static void main(String[] args) {

        jvPalm aJvPalm = new jvPalm();
        aJvPalm.printout(aJvPalm.msgs);
    }
}

