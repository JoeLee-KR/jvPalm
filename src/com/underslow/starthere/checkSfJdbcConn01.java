// TODO

package com.underslow.starthere;

import java.sql.*;
import java.util.Properties;

//class definition
public class checkSfJdbcConn01 {
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
    String jdbcUrl = "jdbc:snowflake://jx75304.ap-northeast-2.aws.snowflakecomputing.com/";
    //String jdbcUrl = "jdbc:snowflake://atixoaj-skbroadband.snowflakecomputing.com/";
    // FOR SKB INTERNAL
    //String jdbcUrl = "jdbc:snowflake://atixoaj-skbroadband.privatelink.snowflakecomputing.com/";
    String sfUser = "xjoelee@sk.com";
    String sfPswd = "VNgkgk00";
    String sfAccount = "atixoaj-skbroadband";
    String sfWarehouse = "WH_PRD_XS";
    String sfDB = "SKB_DEV";    // SKB_PRD
    //String sfSchema = "ANLY_OWN";
    String sfRole = "ACCOUNTADMIN";
    Properties properties;

    // Statis Using Variables
    String selectSQL = "SELECT CURRENT_VERSION() AS fromJAVA";

    //default constructor
    public checkSfJdbcConn01() {
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

        } catch (SQLException exp) {
            exp.printStackTrace();
        }
    }

    public void closeConnection() {
        try{
            sfRS.close();
            sfStmt.close();
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
            System.out.println("\tcheck Query rs Size : " + sizeRS);
        } catch (SQLException exp) {
            exp.printStackTrace();
        }
    }
    public static void main(String[] args) {
        System.out.println("JOE::Snoflake JDBC check test start...");
        //properties object
        checkSfJdbcConn01 my = new checkSfJdbcConn01();
        my.setConnProperties();
        my.getConnection();
        my.getProcessQuery();
        my.closeConnection();

        System.out.println("JOE::Snoflake JDBC check test end...");
    }
}