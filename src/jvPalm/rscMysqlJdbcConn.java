package jvPalm;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class rscMysqlJdbcConn {
    // for MySQL
    String driverClassName00 = "com.mysql.cj.jdbc.Driver";
    String url00 = "jdbc:mysql://palmmysqldb:3306/palmdb";

    // for MARIADB
    String driverClassName01 = "org.mariadb.jdbc.Driver";
    String url01 = "jdbc:mariadb://palmmysqldb:3306/";

    //Class.forName("oracle.jdbc.driver.OracleDriver");    //oracle

    String driverClassName = driverClassName00;
    String url = url00;

    String id = "palmadm";
    String pw = "prom3306!!";

    Connection myConn;
    Statement stmt;
    ResultSet rs;

    String checkSql01 = "select starttime, metric_score from palmdb.palmdemo limit 10";

    public void getConnection(){
        System.out.print("Connectio Info:" + this.toString() );

        try{
            //드라이버 로딩 (Mysql 또는 Oracle 중에 선택하시면 됩니다.)
            Class.forName( driverClassName );    //mysql
            //Class.forName("oracle.jdbc.driver.OracleDriver");    //oracle
        }catch (Exception e){
            e.printStackTrace();
        }
        try{
            //커넥션을 가져온다.
            myConn = DriverManager.getConnection(url, id, pw);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void getCreateStmt(){
        try{
            stmt = myConn.createStatement();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void runQuery( String SQL ){
        try{
            rs = stmt.executeQuery( SQL );
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void closeConnection(){
        try{
            //자원 반환
            rs.close();
            stmt.close();
            myConn.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void printUsage01(){
        System.out.println("Usage: need one arguments");
        System.out.println("0 : use MysqlDB Driver ");
        System.out.println("1 : use MariaDB Driver ");
    }

    int selectJdbcUrl(String[] args){
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
            System.out.println("[Select]: " + args[0] + "[Using JDBC url]:" + url
                    + "Using Driver]:" + driverClassName );
            return (0);
        } catch ( Exception e ) {
            e.printStackTrace();
            return (-1);
        }
    }

    public void getData01(){
        try{
            getConnection();
            getCreateStmt();
            runQuery( checkSql01 );

            while(rs.next()){
                //출력
                System.out.print("\tStartTime: " + rs.getString("starttime"));
                System.out.println("\tMetricScore: " + rs.getString("metric_score"));
            }
            closeConnection();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
