/*
Title:
 */
package jvPalm;

import java.time.*;
import java.sql.*;
import java.util.Properties;

//class definition
public class testSfJdbcConn01 {
    // Static Fixed Variables
    public class sfLine {
        public Connection sfConn;
        public Statement sfStmt;
        public ResultSet sfRS;
    }
    Connection sfConn;
    Statement sfStmt;
    PreparedStatement sfpStmt;
    ResultSet sfRS;
    Connection myConn;
    Statement myStmt;
    PreparedStatement mypStmt;
    ResultSet myRS;

    public static class sfConnString {
        //String jdbcUrl = "jdbc:snowflake://jx75304.ap-northeast-2.aws.snowflakecomputing.com/";
        public String sfjdbcUrl = "jdbc:snowflake://atixoaj-skbroadband.snowflakecomputing.com/";
    }
    String sfjdbcUrl = "jdbc:snowflake://atixoaj-skbroadband.snowflakecomputing.com/";
    String sfUser = "xjoelee@sk.com";
    String sfPswd = "VNgkgk00@@";
    String sfAccount = "atixoaj-skbroadband";
    String sfWarehouse = "WH_PRD_XS";
    String sfDB = "SKB_PRD";
    String sfSchema = "ANLY_OWN";
    String sfRole = "ACCOUNTADMIN";

    String myjdbcUrl = "jdbc:mysql://palm252:3309/palmdb";
    String myUser = "palmadm";
    String myPswd = "prom3306!!";

    // Statis Using Variables
    Properties properties;
    Timestamp workDT;
    String sfselectSQL;
    String myselectSQL;
    String selectQueryHistory;
    String insertQueryHistory;

    //default constructor
    public testSfJdbcConn01() {
        properties = new Properties();
        //sfLine sfline = new sfLine();
        //nowDT = new LocalDateTime();
        //nowDT = new LocalDateTime();

        try{
            //드라이버 로딩 (Mysql 또는 Oracle 중에 선택하시면 됩니다.)
            Class.forName("com.mysql.cj.jdbc.Driver");    //mysql
            //Class.forName("oracle.jdbc.driver.OracleDriver");    //oracle
            //Class.forName("com.snowflake.client.jdbc.SnowflakeDriver");    //snowflake 1
            Class.forName("net.snowflake.client.jdbc.SnowflakeDriver");    //snowflake 2
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setQueries(){
        //sfselectSQL = "SELECT CURRENT_VERSION() AS fromJAVA";
        sfselectSQL = "SELECT CURRENT_TIMESTAMP() AS fromJAVA";
        myselectSQL = "select version() as fromJAVA;";
        selectQueryHistory =
            " SELECT "+
                "QUERY_ID, "+
                "QUERY_TEXT, "+             // text
                "DATABASE_NAME, "+
                "SCHEMA_NAME, "+
                "QUERY_TYPE, "+

                "SESSION_ID, "+             // decimal(38,0)
                "USER_NAME, "+
                "ROLE_NAME, "+
                "WAREHOUSE_NAME, "+
                "WAREHOUSE_SIZE, "+

                "WAREHOUSE_TYPE, "+
                "CLUSTER_NUMBER, "+         // decimal(38,0)
                "EXECUTION_STATUS, "+
                "ERROR_CODE, "+
                "ERROR_MESSAGE, "+

                "START_TIME, "+             // timestamp
                "END_TIME, "+               // timestamp
                "TOTAL_ELAPSED_TIME, "+     // decimal(38,0)
                "BYTES_SCANNED, "+          // decimal(38,0)
                "PERCENTAGE_SCANNED_FROM_CACHE, "+      // float

                "COMPILATION_TIME, "+       // decimal(38,0)
                "EXECUTION_TIME, "+         // decimal(38,0)
                "CREDITS_USED_CLOUD_SERVICES, "+        // float
                "RELEASE_VERSION, "+
                "TRANSACTION_ID, "+         // decimal(38,0)

                "CHILD_QUERIES_WAIT_TIME, "+            // decimal(38,0)
                "ROLE_TYPE, "+
                "CURRENT_TIMESTAMP() AS NOWTS " +
            "FROM SNOWFLAKE.ACCOUNT_USAGE.QUERY_HISTORY "+
            /* static statement
            "WHERE START_TIME >= to_TIMESTAMP('2023-07-06 00:00:00') "+
                "AND START_TIME < to_TIMESTAMP('2023-07-07 00:00:00') "+
            */
            "WHERE START_TIME >= to_TIMESTAMP( date(?) ) "+
                "AND START_TIME < to_TIMESTAMP( dateadd(DAY, 1, date(?)) ) "+
            /*
            --    "AND START_TIME >= to_TIMESTAMP('2023-07-06 09:00:00') "+
            --    AND TOTAL_ELAPSED_TIME >= 120000
            --    AND QUERY_TYPE = 'SELECT'
            --    AND EXECUTION_STATUS <> 'SUCCESS'
            --    AND SCHEMA_NAME <> 'INFORMATION_SCHEMA'
            --    AND USER_NAME = 'xjoelee@sk.com'
             */
            "ORDER BY START_TIME DESC "+
            "LIMIT 123 "+
            "";
            /*
            where 	start_time >= to_timestamp( date(CURRENT_TIMESTAMP()) )
	            and start_time < to_timestamp( dateadd( DAY, 1, date(CURRENT_TIMESTAMP()) ) )
            */
        insertQueryHistory =
            "INSERT INTO palmdb.sf_query_history ( " +
                "QUERY_ID, "+
                "START_TIME, "+
                "EXECUTION_TIME, "+
                //"COMPILATION_TIME, "+
                "INSERTION_TIME "+
            " ) VALUES ( " +
                "?, ?, ?, ?"+
            " )";
    }
    public void setConnProperties() {
        //setting properties
        properties.put("user", sfUser);
        properties.put("password", sfPswd);
        properties.put("account", sfAccount); //account-id followed by cloud region.
        properties.put("warehouse", sfWarehouse);
        properties.put("db", sfDB);
        properties.put("schema", sfSchema);
        properties.put("role", sfRole);
    }

    public void getConnection() {
        try {
            sfConn = DriverManager.getConnection( sfjdbcUrl, properties);
            System.out.println("\tJOE::Connection SF established, connection id : " + sfConn);

            sfStmt = sfConn.createStatement();
            sfStmt.executeQuery("ALTER SESSION SET JDBC_QUERY_RESULT_FORMAT='JSON'");
            System.out.println("\tJOE::Alter Session > JSON statement, object-id : " + sfStmt);

        } catch (SQLException exp) {
            exp.printStackTrace();
        }

        try {
            myConn = DriverManager.getConnection(myjdbcUrl, myUser, myPswd);
            System.out.println("\tJOE::Connection MYSQL established, connection id : " + myConn);

        } catch (SQLException exp) {
            exp.printStackTrace();
        }
    }
    public void closeConnection() {
        try{
            sfRS.close();
            sfStmt.close();
            sfConn.close();

            myRS.close();
            myStmt.close();
            myConn.close();

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void getProcessQuery00() {
        System.out.println("\tJOE::selectSQL = " + sfselectSQL);
        //try-catch block
        try {
            sfStmt = sfConn.createStatement();
            System.out.println("\tJoe::Got the SF statement object, object-id : " + sfStmt);

            sfRS = sfStmt.executeQuery(sfselectSQL);
            while(sfRS.next()) {
                //following rs.getXXX should also change as per your select query
                System.out.println("\tCURRENT_VERSION(): " + sfRS.getString("FROMJAVA"));
            }
        } catch (SQLException exp) {
            exp.printStackTrace();
        }

        try {
            myStmt = myConn.createStatement();
            System.out.println("\tJoe::Got the MYSQL statement object, object-id : " + myStmt);

            myRS = myStmt.executeQuery(myselectSQL);
            while(myRS.next()) {
                //following rs.getXXX should also change as per your select query
                System.out.println("\tCURRENT_VERSION(): " + myRS.getString("fromJAVA"));
            }
        } catch (SQLException exp) {
            exp.printStackTrace();
        }
    }
    public void getProcessQuery01() {
        int progressMark = 0;
        LocalDateTime nowLocalDT = LocalDateTime.now();
        Timestamp startTS = Timestamp.valueOf( nowLocalDT );
        //try-catch block
        try {
            //sfStmt = sfConn.createStatement();
            sfpStmt = sfConn.prepareStatement(selectQueryHistory);
            sfpStmt.setTimestamp(1, startTS );
            sfpStmt.setTimestamp(2, startTS );
            sfRS = sfpStmt.executeQuery();

            System.out.println("\tJoe::Got the statement object, object-id : " + sfpStmt);
            System.out.println(">>Joe::DB dump: startTS: " + startTS);

            while(sfRS.next()) {
                progressMark++;
                if ( progressMark % 100 == 0) System.out.print(".");
                if ( progressMark % 1000 == 0) System.out.println(":" + progressMark);
                //following rs.getXXX should also change as per your select query
                /*
                System.out.print("\t>>");
                System.out.print("|" + sfRS.getString("QUERY_ID") );
                System.out.print("|" + sfRS.getTimestamp("START_TIME") );
                System.out.print("|" + sfRS.getInt("EXECUTION_TIME") );
                System.out.print("|" + sfRS.getInt("COMPILATION_TIME") );
                System.out.print("|" + sfRS.getTimestamp("NOWTS") );
                System.out.println();
                */
                // insert to mysql & with collision handle
                try {
                    mypStmt = myConn.prepareStatement(insertQueryHistory);
                    mypStmt.setString(1,    sfRS.getString("QUERY_ID"));
                    mypStmt.setTimestamp(2, sfRS.getTimestamp("START_TIME"));
                    mypStmt.setInt(3,       sfRS.getInt("EXECUTION_TIME"));
                    mypStmt.setTimestamp(4, sfRS.getTimestamp("NOWTS"));
                    mypStmt.executeUpdate();
                    mypStmt.close();
                } catch (SQLException exp) {
                    /*
                    System.out.println("\t\tINSERT DUP & EXIT:"+
                        sfRS.getString("QUERY_ID") + "|" +
                        sfRS.getTimestamp("START_TIME") + "|" +
                        sfRS.getTimestamp("NOWTS")
                    );
                    break;
                    */
                }
            } //while
            LocalDateTime endLocalDT = LocalDateTime.now();
            Timestamp endTS = Timestamp.valueOf( endLocalDT );
            System.out.println(":" + progressMark);
            System.out.println(">>Joe::DB dump: eeendTS: " + endTS );
        } catch (SQLException exp) {
            exp.printStackTrace();
        }
    }

    public static void main(String[] args) {
        LocalDateTime nowDT;

        nowDT = LocalDateTime.now();
        System.out.println("Oasis.snowflake.dump.2palm.start---" + nowDT);
        //properties object
        testSfJdbcConn01 my = new testSfJdbcConn01();

        my.setQueries();
        my.setConnProperties();
        my.getConnection();
        my.getProcessQuery00();
        my.getProcessQuery01();
        my.closeConnection();

        nowDT = LocalDateTime.now();
        System.out.println("Oasis.snowflake.dump.2palm.end" + nowDT);
    }
}