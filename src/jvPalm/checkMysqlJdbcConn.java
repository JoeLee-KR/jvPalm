package jvPalm;

public class checkMysqlJdbcConn {

    public static void main(String[] args) {
        System.out.println("JOE::Mysql JDBC check test start...");
        rscMysqlJdbcConn aMySS = new rscMysqlJdbcConn();

        if ( args.length == 1 ) {
            if (aMySS.selectJdbcUrl(args) != -1) {
                aMySS.getData01();
            } else aMySS.printUsage01();
        } else System.out.println("\t*Usage: must use one arg.");
        System.out.println("JOE::Mysql JDBC check test end...");
    }
}

