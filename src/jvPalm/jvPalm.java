package jvPalm;

public class jvPalm {
    String[] msgs = new String[] {
            "This JAR file to dump data from (remote)SF to (local)MYSQL.",
            "General command pattern is: ",
            "  $ java -cp \"LIB_PATH/*:JAR_PATH/*\" YOUR_MAIN_METHOD ARGS",
            "  $ java -cp \"/svc/palm/sfAgent/drivers/*:/svc/palm/sfAgent/bin/*\" jvPalm.checkMysqlJdbcConn 0",
            "",
            "included methods are bellow:",
            "  >> jvPalm.checkMysqlJdbcConn [0..3]",
            "     0: mysql://palmmysqldb:3306, 1: mariadb://palmmysqldb:3306",
            "     2: mysql://palmmysqldb:9306, 3: mariadb://palmmysqldb:9306",
            "  >> jvPalm.checkSfJdbcConn [0..3]",
            "     0: Internet atixoaj, 1: Internet jx75304",
            "     2: Private atixoaj,  3: Private jx75304",
            "  >> mvSFM_QueryHistory { [0..1] { (minutes | Date Hour)} }",
            "     no arg: this help message" ,
            "     1 arg: (1)connMode (default now -15 min get" ,
            "     2 args: (1)connMode, (2)now -X min get",
            "     3 args: (1)connMode, (2)target day, (3)target a Hour (hourly(0..23) or 24 is hole day)",
            "     ---1st arg connMode codemap---",
            "     0:SF-Internet-3306, 1:SF-Private-3306, 2:SF-Internet-9306, 3:SF-Private-9306",
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

