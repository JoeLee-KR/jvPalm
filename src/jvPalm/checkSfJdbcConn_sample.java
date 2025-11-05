// TODO
//
package jvPalm;

import java.sql.*;
import java.util.Base64;
import java.util.Properties;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;

//class definition
public class checkSfJdbcConn_sample {
    // Static Fixed Variables
    Connection sfConn;
    Statement sfStmt;
    ResultSet sfRS;

    // more classNames
    // "com.mysql.cj.jdbc.Driver"
    // "com.snowflake.client.jdbc.SnowflakeDriver" - others snowflake
    // "oracle.jdbc.driver.OracleDriver"
    String driverClassName = "net.snowflake.client.jdbc.SnowflakeDriver";
    public String jdbcUrl = "jdbc:snowflake://jx75304.ap-northeast-2.aws.snowflakecomputing.com/";
    //public String jdbcUrl = "jdbc:snowflake://skbsfog-skbroadband.snowflakecomputing.com/";
    String sfUser = "PALMADMIN";
    //String sfPswd = "TESTPSWD";
    public String sfAccount = "jx75304.ap-northeast-2.aws";
    //public String sfAccount = "skbsfog-skbroadband";
    String sfWarehouse = "WH_PRD_XS";
    String sfDB = "SNOWFLAKE";
    String sfRole = "ACCOUNTADMIN";
    //String sfRole = "PRD_USER";
    Properties properties;

    // Statis Using Variables
    String selectSQL = "SELECT CURRENT_VERSION() AS fromJAVA";

    public checkSfJdbcConn_sample() {
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
        //properties.put("password", sfPswd);
        properties.put("account", sfAccount); //account-id followed by cloud region.
        properties.put("warehouse", sfWarehouse);
        properties.put("db", sfDB);
        properties.put("role", sfRole);
        properties.put("ocspFailOpen", "true");
        properties.put("insecureMode", "true");

        properties.put("private_key_file", "../../_keys/rsa_key_nocrypt.p8");

        /*
        PrivateKey privateKey = loadPrivateKey("../../_keys/rsa_key_nocrypt.p8");
        System.out.println("\t::privateKey:" + privateKey);
        if (privateKey != null) {
            properties.put("private_key", privateKey);
        }
        */
    }

    /*
    private PrivateKey loadPrivateKey(String filename) {
        try {
            String key = new String(Files.readAllBytes(new File(filename).toPath()));
            key = key.replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s", "");
            byte[] decoded = Base64.getDecoder().decode(key);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decoded);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePrivate(keySpec);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
     */

    public void getConnection() {
        System.out.println("\t::driver:" + driverClassName );
        System.out.println("\t::connUrl:" + jdbcUrl );
        System.out.println("\t::checkSql:" + selectSQL );
        try {
            System.out.println("\tJOE::getConnection, with jdbcUrl, properties : " + sfConn);
            sfConn = DriverManager.getConnection(jdbcUrl, properties);
            System.out.println("\tJOE::Connection established, connection id : " + sfConn);

            sfStmt = sfConn.createStatement();
            sfStmt.executeQuery("ALTER SESSION SET JDBC_QUERY_RESULT_FORMAT='JSON'");
            System.out.println("\tJOE::Alter Session > JSON statement, object-id : " + sfStmt);
            sfStmt.close();
        } catch (SQLException exp) {
            exp.printStackTrace();
        }
    }

    public void getProcessQuery() {
        try {
            sfStmt = sfConn.createStatement();
            sfRS = sfStmt.executeQuery(selectSQL);
            while(sfRS.next()) {
                System.out.println("\tCURRENT_VERSION(): " + sfRS.getString("FROMJAVA"));
            }
            int sizeRS = 0;
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
            if (sfConn != null) sfConn.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        System.out.println("start...");
        checkSfJdbcConn_sample my = new checkSfJdbcConn_sample();

        my.setConnProperties();
        my.getConnection();
        my.getProcessQuery();
        my.closeConnection();

        System.out.println("end...");
    }
}