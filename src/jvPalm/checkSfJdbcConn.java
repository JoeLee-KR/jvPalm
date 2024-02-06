// TODO
//
package jvPalm;

import java.sql.*;
import java.util.Properties;
import java.lang.Integer;

//class definition
public class checkSfJdbcConn {
    // Static Fixed Variables
    Connection sfConn;
    Statement sfStmt;
    ResultSet sfRS;

    // more classNames
    // "com.mysql.cj.jdbc.Driver"
    // "com.snowflake.client.jdbc.SnowflakeDriver" - others snowflake
    // "oracle.jdbc.driver.OracleDriver"
    String driverClassName = "net.snowflake.client.jdbc.SnowflakeDriver";
    //change this below URL as per your snowflake instance
    //String jdbcUrl = "jdbc:snowflake://jx75304.ap-northeast-2.aws.snowflakecomputing.com/";
    //String jdbcUrl = "jdbc:snowflake://atixoaj-skbroadband.snowflakecomputing.com/";
    // FOR SKB INTERNAL
    public String jdbcUrl ;
    String jdbcUrl00 = "jdbc:snowflake://atixoaj-skbroadband.snowflakecomputing.com/";
    String jdbcUrl01 = "jdbc:snowflake://jx75304.ap-northeast-2.aws.snowflakecomputing.com/";
    String jdbcUrl02 = "jdbc:snowflake://atixoaj-skbroadband.privatelink.snowflakecomputing.com/";
    String jdbcUrl03 = "jdbc:snowflake://jx75304.ap-northeast-2.aws.privatelink.snowflakecomputing.com/";
    String sfUser = "palmadmin";
    String sfPswd = "VNgkgk007";
    // String sfPswd = "prom0909!!";
    public String sfAccount ;
    String sfAccount00 = "atixoaj-skbroadband";
    String sfAccount01 = "jx75304.ap-northeast-2.aws";
    String sfAccount02 = "atixoaj-skbroadband";
    String sfAccount03 = "jx75304.ap-northeast-2.aws";
    String sfWarehouse = "WH_PRD_XS";
    String sfDB = "SNOWFLAKE";
    String sfSchema = "ACCOUNT_USAGE";
    String sfRole = "ACCOUNTADMIN";
    Properties properties;

    // Statis Using Variables
    String selectSQL = "SELECT CURRENT_VERSION() AS fromJAVA";

    //default constructor
    public checkSfJdbcConn() {
        properties = new Properties();
        try{
            Class.forName( driverClassName );    //snowflake 2
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //entry main method
    public void setConnProperties() {
        //setting properties
        properties.put("user", sfUser);
        properties.put("password", sfPswd);
        properties.put("account", sfAccount); //account-id followed by cloud region.
        properties.put("warehouse", sfWarehouse);
        properties.put("db", sfDB);
        //properties.put("schema", sfSchema);
        properties.put("role", sfRole);
        properties.put("ocspFailOpen", "true");
        properties.put("insecureMode", "true");
    }

    public void getConnection() {
        System.out.println("\t::driver:" + driverClassName );
        System.out.println("\t::connUrl:" + jdbcUrl );
        System.out.println("\t::checkSql:" + selectSQL );
        try {
            sfConn = DriverManager.getConnection(jdbcUrl, properties);
            //System.out.println("\tJOE::Connection established, connection id : " + sfConn);

            sfStmt = sfConn.createStatement();
            sfStmt.executeQuery("ALTER SESSION SET JDBC_QUERY_RESULT_FORMAT='JSON'");
            //System.out.println("\tJOE::Alter Session > JSON statement, object-id : " + sfStmt);
            sfStmt.close();
        } catch (SQLException exp) {
            exp.printStackTrace();
        }
    }

    public void closeConnection() {
        try{
            sfConn.close();

        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public void getProcessQuery() {
        try {
            sfStmt = sfConn.createStatement();
            //System.out.println("\tJoe::Got the statement object, object-id : " + sfStmt);

            sfRS = sfStmt.executeQuery(selectSQL);
            while(sfRS.next()) {
                //following rs.getXXX should also change as per your select query
                System.out.println("\tCURRENT_VERSION(): " + sfRS.getString("FROMJAVA"));
            }
            int sizeRS = 0;
            //sfRS.last();
            sizeRS = sfRS.getRow();
            sfStmt.close();
            sfRS.close();
            System.out.println("\tcheck Query rs Size : " + sizeRS);
        } catch (SQLException exp) {
            exp.printStackTrace();
        }
    }

    void printUsage(){
        System.out.println("Usage: need one arguments");
        System.out.println("0 : " + jdbcUrl00 +"|"+ sfAccount00);
        System.out.println("1 : " + jdbcUrl01 +"|"+ sfAccount01);
        System.out.println("2 : " + jdbcUrl02 +"|"+ sfAccount02);
        System.out.println("3 : " + jdbcUrl03 +"|"+ sfAccount03);
    }

    int setupJdbcUrl(String[] args){
        try {
            switch (Integer.parseInt(args[0])) {
                case 0: // default
                    jdbcUrl = jdbcUrl00;
                    sfAccount = sfAccount00;
                    break;
                case 1: // selected
                    jdbcUrl = jdbcUrl01;
                    sfAccount = sfAccount01;
                    break;
                case 2: // selected
                    jdbcUrl = jdbcUrl02;
                    sfAccount = sfAccount02;
                    break;
                case 3: // selected
                    jdbcUrl = jdbcUrl03;
                    sfAccount = sfAccount03;
                    break;
                default:
                    return (-1);
            }
            System.out.println("\t[Select]: " + args[0] + "[Using JDBC url]:" + jdbcUrl);
            return (0);
        } catch ( Exception e ) {
            e.printStackTrace();
            return (-1);
        }
    }

    public static void main(String[] args) {
        System.out.print("JOE::Snoflake JDBC check test, with jdbcUrl, start...");
        System.out.println("w/ocspFailOpen=true, w/insecureMode=true");

        checkSfJdbcConn my = new checkSfJdbcConn();

        if ( args.length == 1 ) {
            if (my.setupJdbcUrl(args) != -1) {
                my.setConnProperties();
                my.getConnection();
                my.getProcessQuery();
                my.closeConnection();
            } else my.printUsage();
        } else my.printUsage();

        System.out.println("JOE::Snoflake JDBC check test end...");
    }
}