package jvTest;

public class skeleton {
}

package jvPalm;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.sql.*;
        import java.util.Base64;
import java.util.Properties;

public class checkSfJdbcConn_sample {
    Connection sfConn;
    Statement sfStmt;
    ResultSet sfRS;

    String driverClassName = "net.snowflake.client.jdbc.SnowflakeDriver";
    String jdbcUrl = "jdbc:snowflake://skbsfog-skbroadband.snowflakecomputing.com/";
    String sfUser = "TESTID";
    String sfAccount = "skbsfog-skbroadband";
    String sfWarehouse = "WH_PRD_XS";
    String sfDB = "SNOWFLAKE";
    String sfRole = "ACCOUNTADMIN";
    Properties properties;

    String selectSQL = "SELECT CURRENT_VERSION() AS fromJAVA";

    public checkSfJdbcConn_sample() {
        properties = new Properties();
        try {
            Class.forName(driverClassName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ✅ KeyPair 방식 설정
    public void setConnProperties() {
        properties.put("user", sfUser);
        properties.put("account", sfAccount);
        properties.put("warehouse", sfWarehouse);
        properties.put("db", sfDB);
        properties.put("role", sfRole);
        properties.put("ocspFailOpen", "true");
        properties.put("insecureMode", "true");

        // ❗ RSA 개인키 파일 경로 지정
        PrivateKey privateKey = loadPrivateKey("/path/to/rsakey.p8");
        if (privateKey != null) {
            properties.put("private_key", privateKey);
        }
    }

    // ✅ PKCS#8 PEM → PrivateKey 객체 변환 메서드
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

    public void getConnection() {
        System.out.println("\t::driver:" + driverClassName);
        System.out.println("\t::connUrl:" + jdbcUrl);
        System.out.println("\t::checkSql:" + selectSQL);
        try {
            sfConn = DriverManager.getConnection(jdbcUrl, properties);

            sfStmt = sfConn.createStatement();
            sfStmt.executeQuery("ALTER SESSION SET JDBC_QUERY_RESULT_FORMAT='JSON'");
            sfStmt.close();
        } catch (SQLException exp) {
            exp.printStackTrace();
        }
    }

    public void getProcessQuery() {
        try {
            sfStmt = sfConn.createStatement();
            sfRS = sfStmt.executeQuery(selectSQL);
            while (sfRS.next()) {
                System.out.println("\tCURRENT_VERSION(): " + sfRS.getString("FROMJAVA"));
            }
            sfStmt.close();
            sfRS.close();
        } catch (SQLException exp) {
            exp.printStackTrace();
        }
    }

    public void closeConnection() {
        try {
            if (sfConn != null) sfConn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        System.out.print("start...");
        checkSfJdbcConn_sample my = new checkSfJdbcConn_sample();

        my.setConnProperties();
        my.getConnection();
        my.getProcessQuery();
        my.closeConnection();
        System.out.println("end...");
    }
}
