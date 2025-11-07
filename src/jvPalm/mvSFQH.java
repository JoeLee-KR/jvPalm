/*
Title: Fetch data at Snowflake, and dump to mysql
12 July 2023:
 - snowflake.query_history dump
 - allow args: {min} & {date + hour }
 */
package jvPalm;

import java.sql.*;
import java.util.Base64;
import java.util.Properties;
import java.time.LocalDate;
import java.time.LocalDateTime;

import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;

//class definition
public class mvSFQH {
    // Static Fixed Variables
    Connection sfConn;
    Statement sfStmt;
    ResultSet sfRS;
    PreparedStatement sfpStmt;

    Connection myConn;
    Statement myStmt;
    PreparedStatement mypStmt;
    ResultSet myRS;

    // general jdbc variables
    String classNameJdbcMysql = "com.mysql.cj.jdbc.Driver";
    String classNameJdbcSnowflake = "net.snowflake.client.jdbc.SnowflakeDriver";

    // FOR PUBLIC INTERNET
    String sfjdbcUrl00 = "jdbc:snowflake://jx75304.ap-northeast-2.aws.snowflakecomputing.com";
    String sfjdbcUrl02 = "jdbc:snowflake://skbsfog-skbroadband.snowflakecomputing.com/";
    // FOR SKB INTERNAL PRIVATE NETWORK
    String sfjdbcUrl01 = "jdbc:snowflake://jx75304.ap-northeast-2.privatelink.snowflakecomputing.com/";
    String sfjdbcUrl03 = "jdbc:snowflake://skbsfog-skbroadband.privatelink.snowflakecomputing.com/";
    String sfjdbcUrl;

    String sfUser = "PALMADMIN";
    //String sfPswd = "YOUR_PASSWORD";
    String sfAccount = "jx75304";
    String sfWarehouse = "WH_PRD_XS";
    String sfDB = "SNOWFLAKE";
    String sfSchema = "ACCOUNT_USAGE";
    String sfRole = "ACCOUNTADMIN"; // check ROLE, USER has ROLE

    //String myjdbcUrl = "jdbc:mysql://palmmysqldb:3306/palmdb";
    // FOR SKB INTERNAL, 9306 for palm252 dev-pc
    String myjdbcUrl00 = "jdbc:mysql://palmmysqldb:3306/palmdb";
    String myjdbcUrl01 = "jdbc:mysql://palmmysqldb:9306/palmdb";
    String myjdbcUrl;
    String myUser = "palmadm";
    String myPswd = "prom3306!!";

    // Statis Using Variables
    Properties properties;

    String sfselectSQL;
    String myselectSQL;
    String selectQueryHistory;
    String insertQueryHistory;

    // working datetimes
    Timestamp selectFromTS;
    Timestamp selectToTS;
    int tsRange;
    Boolean flagDupProcess;

    //default constructor
    public mvSFQH() {
        properties = new Properties();

        try{
            Class.forName( classNameJdbcMysql );    //mysql
            Class.forName( classNameJdbcSnowflake );    //snowflake
        }catch (Exception e){
            e.printStackTrace();
        }
    }  // initiator

    public void SnowflakeJdbcVersionDetail() {
    // check current used Snowflake JDBC version
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

    public void setQueries(){
        sfselectSQL = "SELECT CURRENT_VERSION() AS fromJAVA";
        //sfselectSQL = "SELECT CURRENT_TIMESTAMP() AS fromJAVA";
        myselectSQL = "select version() as fromJAVA;";
        selectQueryHistory = " SELECT " +
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
            "CURRENT_TIMESTAMP() AS NOWTS " +       // timestamp
        "FROM SNOWFLAKE.ACCOUNT_USAGE.QUERY_HISTORY " +
        "WHERE  START_TIME >= to_TIMESTAMP( ? ) " +
                "AND START_TIME < to_TIMESTAMP( ? ) "+
        "ORDER BY START_TIME DESC " +
        "-- LIMIT 12";

        /*
        selectQueryHistory02_day =
            "WHERE  START_TIME >= to_TIMESTAMP( date(?) ) "+
                "AND START_TIME < to_TIMESTAMP( dateadd(DAY, 1, date(?) ) ) ";

        selectQueryHistory02_dayhour =
            "WHERE  START_TIME >= to_TIMESTAMP( dateadd(HOUR, ?,   date(?)) ) "+
                "AND START_TIME < to_TIMESTAMP( dateadd(hour, ?+1, date(?)) ) ";

        selectQueryHistory02_nowmin =
            "WHERE  START_TIME >= to_TIMESTAMP( ? ) "+
                "AND START_TIME < to_TIMESTAMP( dateadd(MINUTE, -(?), date(?)) ) ";

        selectQueryHistory03 =
            "ORDER BY START_TIME DESC "+
            "-- LIMIT 12 ";
*/
        insertQueryHistory = "INSERT INTO palmdb.sf_query_history ( " +
            "QUERY_ID," +
            "QUERY_TEXT, " +            // text
            "DATABASE_NAME," +
            "SCHEMA_NAME," +
            "QUERY_TYPE," +

            "SESSION_ID," +             //  -- BigDecimal at Java
            "USER_NAME," +
            "ROLE_NAME," +
            "WAREHOUSE_NAME," +
            "WAREHOUSE_SIZE," +

            "WAREHOUSE_TYPE," +
            "CLUSTER_NUMBER," +         // number   -- LONG
            "EXECUTION_STATUS," +
            "ERROR_CODE," +
            "ERROR_MESSAGE," +

            "START_TIME," +             // timestamp
            "END_TIME," +               // timestamp
            "TOTAL_ELAPSED_TIME," +     // number  -- LONG
            "BYTES_SCANNED," +          // number  -- LONG
            "PERCENTAGE_SCANNED_FROM_CACHE," +  // MUST DOUBLE

            "COMPILATION_TIME," +       // number  -- LONG
            "EXECUTION_TIME," +         // number  -- LONG
            "CREDITS_USED_CLOUD_SERVICES," +    // MUST DOUBLE
            "RELEASE_VERSION," +
            "TRANSACTION_ID," +         // decimal(38,0) -- BigDemical at Java

            "CHILD_QUERIES_WAIT_TIME," +    // number  -- LONG
            "ROLE_TYPE," +
            "INSERTION_TIME," +          // timestamp, NOWTS
            "STARTTIME" +               // timestamp, StartTime cut seconds
        " ) VALUES ( " +
            "?, ?, ?, ?, ?," +
            "?, ?, ?, ?, ?," +
            "?, ?, ?, ?, ?," +
            "?, ?, ?, ?, ?," +
            "?, ?, ?, ?, ?," +
            "?, ?, ?, date_format( timestamp(?), '%Y-%m-%d %H:%i:00' ) " +
                // timestamp( date_format(now(), "%Y-%m-%d %H:%i:00")
                // date_format( timestamp(now()),  '%Y-%m-%d %H:%i:00')
        ")";    // insertQueryHistory
    } // setQuerie Strings

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
        try {
            sfConn = DriverManager.getConnection( sfjdbcUrl, properties);
            //System.out.println("\tJOE::Connection SF established, connection id : " + sfConn);
        } catch (SQLException exp) {
            exp.printStackTrace();
        }

        try {
            myConn = DriverManager.getConnection(myjdbcUrl, myUser, myPswd);
            //System.out.println("\tJOE::Connection MYSQL established, connection id : " + myConn);
        } catch (SQLException exp) {
            exp.printStackTrace();
        }
    }

    public void closeConnection() {
        try{
            sfpStmt.close();
            sfRS.close();
            sfConn.close();

            mypStmt.close();
            myConn.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void getProcessQuery01() {
        int progressMark = 0;
        int firstProgressMark = 1;

        try {
            // make selectQueryString by arg case.
            sfpStmt = sfConn.prepareStatement(selectQueryHistory);
            sfpStmt.setTimestamp(1, selectFromTS );
            sfpStmt.setTimestamp(2, selectToTS );
            sfRS = sfpStmt.executeQuery();

            // make insertQueryString with ResultSet of select
            mypStmt = myConn.prepareStatement(insertQueryHistory);

            System.out.println("::DB dump start: " + LocalDateTime.now() );
            while(sfRS.next()) {
                progressMark++;
                if ( progressMark == 10 || progressMark == 100 )
                    { firstProgressMark = progressMark; System.out.println(":" + progressMark);}
                if ( progressMark % firstProgressMark == 0) System.out.print(".");
                if ( progressMark % 1000 == 0) System.out.println(":" + progressMark);
                //following rs.getXXX should also change as per your select query

                // insert to mysql & with collision handle
                try {
                    // make pstmt for insertSQL
                    mypStmt.setString(1,    sfRS.getString("QUERY_ID"));
                    mypStmt.setString(2,    sfRS.getString("QUERY_TEXT"));
                    mypStmt.setString(3,    sfRS.getString("DATABASE_NAME"));
                    mypStmt.setString(4,    sfRS.getString("SCHEMA_NAME"));
                    mypStmt.setString(5,    sfRS.getString("QUERY_TYPE"));

                    mypStmt.setBigDecimal(6,sfRS.getBigDecimal("SESSION_ID"));
                    mypStmt.setString(7,    sfRS.getString("USER_NAME"));
                    mypStmt.setString(8,    sfRS.getString("ROLE_NAME"));
                    mypStmt.setString(9,    sfRS.getString("WAREHOUSE_NAME"));
                    mypStmt.setString(10,   sfRS.getString("WAREHOUSE_SIZE"));

                    mypStmt.setString(11,   sfRS.getString("WAREHOUSE_TYPE"));
                    mypStmt.setLong(12,     sfRS.getLong("CLUSTER_NUMBER"));
                    mypStmt.setString(13,   sfRS.getString("EXECUTION_STATUS"));
                    mypStmt.setString(14,   sfRS.getString("ERROR_CODE"));
                    mypStmt.setString(15,   sfRS.getString("ERROR_MESSAGE"));

                    mypStmt.setTimestamp(16,sfRS.getTimestamp("START_TIME"));
                    mypStmt.setTimestamp(17,sfRS.getTimestamp("END_TIME"));
                    mypStmt.setLong(18,     sfRS.getLong("TOTAL_ELAPSED_TIME"));
                    mypStmt.setLong(19,     sfRS.getLong("BYTES_SCANNED"));
                    mypStmt.setDouble(20,   sfRS.getDouble("PERCENTAGE_SCANNED_FROM_CACHE"));

                    mypStmt.setLong(21,     sfRS.getLong("COMPILATION_TIME"));
                    mypStmt.setLong(22,     sfRS.getLong("EXECUTION_TIME"));
                    //mypStmt.setBigDecimal(23,    sfRS.getBigDecimal("CREDITS_USED_CLOUD_SERVICES"));
                    //tmpDouble = (double)sfRS.getBigDecimal("CREDITS_USED_CLOUD_SERVICES");
                    mypStmt.setDouble(23,   sfRS.getDouble("CREDITS_USED_CLOUD_SERVICES")); // 1.46E-4, 12.34f
                    mypStmt.setString(24,   sfRS.getString("RELEASE_VERSION"));
                    mypStmt.setBigDecimal(25,sfRS.getBigDecimal("TRANSACTION_ID"));

                    mypStmt.setLong(26,     sfRS.getLong("CHILD_QUERIES_WAIT_TIME"));
                    mypStmt.setString(27,   sfRS.getString("ROLE_TYPE"));
                    mypStmt.setTimestamp(28,sfRS.getTimestamp("NOWTS")); // =INSERTION_TIME
                    mypStmt.setTimestamp(29,sfRS.getTimestamp("START_TIME") );
                    // timestamp( date_format(now(), "%Y-%m-%d %H:%i:00")

                    mypStmt.executeUpdate();
                } catch (SQLException exp) {
                    // case with args
                    // case 3 now : break
                    // case 1,2 day or day-hour : no break
                    /*
                    System.out.println("\t\tINSERT DUP & EXIT:"+
                        sfRS.getString("QUERY_ID") + "|" +
                        sfRS.getTimestamp("START_TIME") + "|" +
                        sfRS.getTimestamp("NOWTS")
                    );
                    */
                    if ( !flagDupProcess ) {
                        progressMark--;
                        break;
                    }
                } // catch
            } //while
            mypStmt.close();
            sfpStmt.close();

            System.out.println();
            System.out.println("::DB dump end: " + LocalDateTime.now() + ", Progress Rows: " + progressMark );
        } catch (SQLException exp) {
            exp.printStackTrace();
        }
    }

    public Timestamp makeTS(String strDate){
        return Timestamp.valueOf( LocalDate.parse(strDate).atTime(0,0,0) );
    }

    public Timestamp makeTS(LocalDateTime staticDT){
        return Timestamp.valueOf( staticDT );
    }

    public int setSFandMyURL(String x){
        if ( Integer.parseInt( x ) == 0 )         { sfjdbcUrl = sfjdbcUrl00; myjdbcUrl=myjdbcUrl00;}
        else if ( Integer.parseInt( x ) == 1 )    { sfjdbcUrl = sfjdbcUrl01; myjdbcUrl=myjdbcUrl00;}
        else if ( Integer.parseInt( x ) == 2 )    { sfjdbcUrl = sfjdbcUrl00; myjdbcUrl=myjdbcUrl01;}
        else if ( Integer.parseInt( x ) == 3 )    { sfjdbcUrl = sfjdbcUrl01; myjdbcUrl=myjdbcUrl01;}
        else if ( Integer.parseInt( x ) == 4 )    { sfjdbcUrl = sfjdbcUrl02; myjdbcUrl=myjdbcUrl00;}
        else if ( Integer.parseInt( x ) == 5 )    { sfjdbcUrl = sfjdbcUrl03; myjdbcUrl=myjdbcUrl00;}
        else if ( Integer.parseInt( x ) == 6 )    { sfjdbcUrl = sfjdbcUrl02; myjdbcUrl=myjdbcUrl01;}
        else if ( Integer.parseInt( x ) == 7 )    { sfjdbcUrl = sfjdbcUrl03; myjdbcUrl=myjdbcUrl01;}
        else return(-1);
        return(0);
    }

    public void printHelp01(){
        System.out.println("Usage_: mvSFM_QueryHistory { [0..1] { (minutes | Date Hour)} }");
        System.out.println("\tno arg: this help message");
        System.out.println("\t1 arg: (1)connMode (default now -15 min get");
        System.out.println("\t2 args: (1)connMode, (2)now -X min get");
        System.out.println("\t3 args: (1)connMode, (2)target day, (3)target a Hour (hourly(0..23) or 24 is hole day)");
        System.out.println("\t  --1st arg connMode codemap, with SF Locator URL(jx75304) ---");
        System.out.println("\t0:SF-Pub.jx75304-3306, 1:SF-Pri.jx75304-3306, 2:SF-Pub.jx75304-9306, 3:SF-Pri.jx75304-9306");
        System.out.println("\t4:SF-Pub.skbsfog-3306, 5:SF-Pri.skbsfog-3306, 6:SF-Pub.skbsfog-9306, 7:SF-Pri.skbsfog-9306");
        System.out.println("\t  --[251103] java jvPalm.mvSFQH 2(6), for develeop notebook+EX-mysql");
        System.out.println("\t  --[251103] java jvPalm.mvSFQH 0(4), for develeop palm252 Server+IN-mysql");
        System.out.println("\t  --[251103] java jvPalm.mvSFQH 1(5), for opeation Serve+IN-mysql");
    }

    public int getArgs(String[] args) {
        switch(args.length){
            case 0: // minutes now -15, with default URL
                // printHelp01();
                return(-1);
            case 1: // 1 arg: minutes now -15, with URL2SF & MySQL
                if ( setSFandMyURL( args[0] ) == -1 ) return(-1);
                try {
                    tsRange = 15;
                    selectFromTS = Timestamp.valueOf(makeTS( LocalDateTime.now() ).toLocalDateTime().plusMinutes(-tsRange) );
                    selectToTS = makeTS( LocalDateTime.now() );
                    flagDupProcess = false;
                    System.out.println(">>mode:1, now-15min, setURL: " + sfjdbcUrl + ":" + myjdbcUrl + ":" + sfAccount +"<<");
                    System.out.println(">>mode:1, "+selectFromTS+" > "+selectToTS+", no overDUP<<");
                    break;
                } catch ( Exception e ) {
                    return(-1);
                }
            case 2: // 2 args: minutes now -m, 1st:URL, 2nd:m
                if ( setSFandMyURL( args[0] ) == -1 ) return(-1);
                try {
                    tsRange = Integer.parseInt( args[1] );
                    selectFromTS = Timestamp.valueOf(makeTS( LocalDateTime.now() ).toLocalDateTime().plusMinutes(-tsRange) );
                    selectToTS = makeTS( LocalDateTime.now() );
                    flagDupProcess = true;
                    System.out.println(">>mode:2, now-" + tsRange + "min, setURL: " + sfjdbcUrl + ":" + myjdbcUrl +"<<");
                    System.out.println(">>mode:2, "+selectFromTS+" > "+selectToTS+", w/overDUP<<");
                    break;
                } catch (Exception e) {
                    //System.out.println("JOE NUMNER FORMAT ERROR:" + e);
                    return(-1);
                }
            case 3: // 3 args: 1st:URL, 2nd: target yyyy-mm-dd, 3rd: Hourly or 24-all
                if ( setSFandMyURL( args[0] ) == -1 ) return(-1);
                try {
                    tsRange = Integer.parseInt( args[2] );
                    if ( tsRange<24 ) {   // range 1 hour
                        selectFromTS = Timestamp.valueOf(makeTS( args[1] ).toLocalDateTime().plusHours(tsRange) );
                        selectToTS = Timestamp.valueOf(makeTS( args[1] ).toLocalDateTime().plusHours(tsRange+1) );
                    } else {            // range 24 hour, 1day
                        selectFromTS = makeTS( args[1] );
                        selectToTS = Timestamp.valueOf(makeTS( args[1] ).toLocalDateTime().plusDays(1) );
                    }
                    flagDupProcess = true;
                    System.out.println(">>mode:3, " + tsRange + "oclock+1hour, setURL: " + sfjdbcUrl + ":" + myjdbcUrl +"<<");
                    System.out.println(">>mode:3, "+selectFromTS+" > "+selectToTS+", w/overDUP <<");
                    break;
                } catch (Exception e) {
                    //System.out.println("JOE:" + e);
                    return(-1);
                }
            default:
                return (-1);
        }
        System.out.println("\tSelect FromTS: " + selectFromTS + ",ToTS: " + selectToTS + ", tsRange: " + tsRange );
        return (0);
    }  // get args

    public static void main(String[] args) {
        System.out.println("Oasis.snowflake.dump.2palm.start---"+ LocalDateTime.now());

        mvSFQH my = new mvSFQH();
        my.SnowflakeJdbcVersionDetail();

        if ( my.getArgs(args) == -1) {
            my.printHelp01();
        } else {
            my.setQueries();
            my.setConnProperties();
            my.getConnection();
            my.getProcessQuery01();
            my.closeConnection();
        } // args

        System.out.println("Oasis.snowflake.dump.2palm.end---"+ LocalDateTime.now());
    }
}