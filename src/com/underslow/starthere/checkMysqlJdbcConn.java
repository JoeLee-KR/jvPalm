package com.underslow.starthere;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class checkMysqlJdbcConn {
    Connection con;
    Statement stmt;
    ResultSet rs;

    String driverClassName;
    String url;

    // for MySQL
    String driverClassName00 = "com.mysql.cj.jdbc.Driver";
    String url00 = "jdbc:mysql://palmmysqldb:3306/palmdb";

    // for MARIADB
    String driverClassName01 = "org.mariadb.jdbc.Driver";
    String url01 = "jdbc:mariadb://palmmysqldb:3306/";

    String id = "palmadm";
    String pw = "prom3306!!";
    String checkSql = "select starttime, metric_score from palmdb.palmdemo limit 10";

    public checkMysqlJdbcConn(){

    }

    public void getConnection(){
        System.out.println("\t::driver:" + driverClassName );
        System.out.println("\t::connUrl:" + url );
        System.out.println("\t::checkSql:" + checkSql );
        System.out.println("\t::user id:" + id );
        try{
            //드라이버 로딩 (Mysql 또는 Oracle 중에 선택하시면 됩니다.)
            Class.forName( driverClassName );    //mysql
            //Class.forName("oracle.jdbc.driver.OracleDriver");    //oracle
        }catch (Exception e){
            e.printStackTrace();
        }
        try{
            //커넥션을 가져온다.
            con = DriverManager.getConnection(url, id, pw);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void getData(){
        try{
            stmt = con.createStatement();
            //데이터를 가져온다.
            rs = stmt.executeQuery( checkSql );

            while(rs.next()){
                //출력
                System.out.print("\tStartTime: " + rs.getString("starttime"));
                System.out.println("\tMetricScore: " + rs.getString("metric_score"));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void closeConnection(){
        try{
            //자원 반환
            rs.close();
            stmt.close();
            con.close();

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public final void printUsage(){
        System.out.println("Usage: need one arguments");
        System.out.println("0 : use MysqlDB Driver " + driverClassName00 + "|" + url00);
        System.out.println("1 : use MariaDB Driver " + driverClassName01 + "|" + url01);
    }

    int setupJdbcUrl(String[] args){
        try {
            switch (Integer.parseInt(args[0])) {
                case 0: // default
                    url = url00;
                    driverClassName = driverClassName00;
                    break;
                case 1: // selected
                    url = url01;
                    driverClassName = driverClassName01;
                    break;
                default:
                    return (-1);
            }
            System.out.println("\t[Select]: " + args[0] + "[Using JDBC url]:" + url
                + "Using Driver]:" + driverClassName );
            return (0);
        } catch ( Exception e ) {
            e.printStackTrace();
            return (-1);
        }
    }

    public static void main(String[] args) {
        System.out.println("JOE::Mysql JDBC check test start...");

        if ( args.length == 1 ) {
            checkMysqlJdbcConn my = new checkMysqlJdbcConn();
            if (my.setupJdbcUrl(args) != -1) {
                my.getConnection();
                my.getData();
                my.closeConnection();
            } else my.printUsage();
        } else System.out.println("\tUsage: must use one arg.");
        System.out.println("JOE::Mysql JDBC check test end...");
    }
}

