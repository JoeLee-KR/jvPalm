// TODO
//
package jvPalm;

import java.sql.*;
import java.util.Base64;
import java.util.Properties;
import java.lang.Integer;

import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;

//class definition
public class checkSfJdbcConn {
    // Static Fixed Variables
    Connection sfConn;
    Statement sfStmt;
    ResultSet sfRS;

    // more some JDBC classNames
    // "com.mysql.cj.jdbc.Driver"
    // "com.snowflake.client.jdbc.SnowflakeDriver" - others snowflake
    // "oracle.jdbc.driver.OracleDriver"
    String driverClassName = "net.snowflake.client.jdbc.SnowflakeDriver";

    // snowflake Endpoint URL: Account-EP
    //String jdbcUrl = "jdbc:snowflake://jx75304.ap-northeast-2.aws.snowflakecomputing.com/";
    // snowflake Endpoint URL: ORG-EP
    //String jdbcUrl = "jdbc:snowflake://skbsfog-skbroadband.snowflakecomputing.com/";
    // Recommand, use Account-EP URL
    String jdbcUrl00 = "jdbc:snowflake://skbsfog-skbroadband.snowflakecomputing.com/";
    String jdbcUrl01 = "jdbc:snowflake://jx75304.ap-northeast-2.aws.snowflakecomputing.com/";
    String jdbcUrl02 = "jdbc:snowflake://skbsfog-skbroadband.privatelink.snowflakecomputing.com/";
    String jdbcUrl03 = "jdbc:snowflake://jx75304.ap-northeast-2.privatelink.snowflakecomputing.com/";
    public String jdbcUrl ;

    String sfUser = "PALMADMIN";
    // String sfPswd = "SOME_PASSWORD";

    // Recommand, use General Account Name, ACCOUNT_NAME differ to ACCOUNT_URL
    // ACCOUNT_NAME is jx75304
    String sfAccount00 = "skbsfog-skbroadband";
    String sfAccount01 = "jx75304";
    public String sfAccount ;

    String sfWarehouse = "WH_PRD_XS";
    String sfDB = "SNOWFLAKE";
    //String sfSchema = "ACCOUNT_USAGE";
    String sfRole = "ACCOUNTADMIN";  // check ROLE, USER has ROLE
    //String sfRole = "PRD_USER";"
    Properties properties;

    // Statis Using Query String
    String selectSQL = "SELECT CURRENT_VERSION() AS fromJAVA";

    //default constructor
    public checkSfJdbcConn() {
        properties = new Properties();
        try{
            Class.forName( driverClassName );    //snowflake
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    // check current used Snowflake JDBC version
    public void SnowflakeJdbcVersionDetail() {
        Package pkg = net.snowflake.client.jdbc.SnowflakeDriver.class.getPackage();
        System.out.println("SNOWFLAKE Driver pkg: " + pkg.getImplementationVersion());

        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver d = drivers.nextElement();
            System.out.println("Loaded driver: " + d.getClass().getName() +
                    "(VER: " + d.getMajorVersion() + "." + d.getMinorVersion() + ") " +
                    d.getClass().getProtectionDomain().getCodeSource().getLocation()
            );
        }
    }

    //entry main method
    public void setConnProperties() {
        //setting properties
        properties.put("user", sfUser);
        //properties.put("password", sfPswd);
        properties.put("account", sfAccount); //account-id followed by cloud region.
        properties.put("warehouse", sfWarehouse);
        properties.put("db", sfDB);
        //properties.put("schema", sfSchema);
        properties.put("role", sfRole);
        properties.put("ocspFailOpen", "true");
        properties.put("insecureMode", "true");

        // WARNING: KEY_PAIR file must located at ./_keys directory of RUNNING BINARY
        properties.put("private_key_file", "./_keys/key_nocrypt_PALMTEST.p8");
    }

    public void getConnection() {
        System.out.println("\t::driver:" + driverClassName );
        System.out.println("\t::connUrl+sfAccount:" + jdbcUrl + "::" + sfAccount );
        System.out.println("\t::checkSql:" + selectSQL );
        System.out.println("\t::properties.privatekey:" + properties.getProperty("private_key_files"));
        try {
            sfConn = DriverManager.getConnection(jdbcUrl, properties);
            //System.out.println("\tJOE::Connection established, connection : " + sfConn);

            sfStmt = sfConn.createStatement();
            sfStmt.executeQuery("ALTER SESSION SET JDBC_QUERY_RESULT_FORMAT='JSON'");
            //System.out.println("\tJOE::Alter Session > JSON statement, object-id : " + sfStmt);
            sfStmt.close();
        } catch (SQLException exp) {
            exp.printStackTrace();
        }
    }

    public void getProcessQuery() {
        try {
            sfStmt = sfConn.createStatement();
            //System.out.println("\tJoe::Got the statement object, object-id : " + sfStmt);

            sfRS = sfStmt.executeQuery(selectSQL);
            while(sfRS.next()) {
                //following rs.getXXX should also change aligned used query statement
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

    public void closeConnection() {
        try{
            sfConn.close();

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    void printUsage(){
        System.out.println("Usage: need one arguments");
        System.out.println("0 public: " + jdbcUrl00 +"|"+ sfAccount00);
        System.out.println("1 public: " + jdbcUrl01 +"|"+ sfAccount01);
        System.out.println("2 private: " + jdbcUrl02 +"|"+ sfAccount00);
        System.out.println("3 private: " + jdbcUrl03 +"|"+ sfAccount01);
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
                    sfAccount = sfAccount00;
                    break;
                case 3: // selected
                    jdbcUrl = jdbcUrl03;
                    sfAccount = sfAccount01;
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
        my.SnowflakeJdbcVersionDetail();

        if ( args.length == 1 ) {
            if (my.setupJdbcUrl(args) != -1) {
                my.setConnProperties();
                my.getConnection();
                my.getProcessQuery();
                my.closeConnection();
            } else my.printUsage();
        } else my.printUsage();

        System.out.println("JOE::Snowflake JDBC check test end...");
    }
}