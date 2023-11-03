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
            "  >> jvPalm.checkSfJdbcConn [0..3]",
            "     0,1: Internet atixoaj,jx75304, 2,3: Private atixoaj,jx75304",
            "  >> jvPalm.mvSFM_QueryHistory [0..1] (minutes | Date Hour)",
            "     First 1 Arg is must Conn option: 0:Internet, 1:Private",
            "     2nd arg is 15 minutes for default, or arg 1 mean arg minutes, and break at DUP",
            "     2nd & 3rd Args are Date & Hour(0..23,24) args, 24 means hole day and not break at DUP"
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

