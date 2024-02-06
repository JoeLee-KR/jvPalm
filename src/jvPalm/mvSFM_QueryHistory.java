/*
Title: Fetch data at Snowflake, and dump to mysql
12 July 2023:
 - snowflake.query_history dump
 - allow args: {min} & {date + hour }
 */
package jvPalm;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Properties;

//class definition
public class mvSFM_QueryHistory {
    // Static Fixed Variables
    Connection sfConn;
    Statement sfStmt;
    PreparedStatement sfpStmt;
    ResultSet sfRS;

    Connection myConn;
    Statement myStmt;
    PreparedStatement mypStmt;
    ResultSet myRS;

    // general jdbc variables
    String classNameJdbcMysql = "com.mysql.cj.jdbc.Driver";
    String classNameJdbcSnowflake = "net.snowflake.client.jdbc.SnowflakeDriver";

    String sfjdbcUrl;
    // FOR INTERNET
    //String sfjdbcUrl = "jdbc:snowflake://jx75304.ap-northeast-2.aws.snowflakecomputing.com/";
    String sfjdbcUrl00 = "jdbc:snowflake://atixoaj-skbroadband.snowflakecomputing.com/";

    // FOR SKB INTERNAL PRIVATE
    String sfjdbcUrl01 = "jdbc:snowflake://atixoaj-skbroadband.privatelink.snowflakecomputing.com/";

    String sfUser = "palmadmin";
    String sfPswd = "VNgkgk007";
    String sfAccount = "atixoaj-skbroadband";
    String sfWarehouse = "WH_PRD_XS";
    String sfDB = "SNOWFLAKE";
    String sfSchema = "ACCOUNT_USAGE";
    String sfRole = "ACCOUNTADMIN";

    //String myjdbcUrl = "jdbc:mysql://palmmysqldb:3306/palmdb";
    // FOR SKB INTERNAL
    String myjdbcUrl = "jdbc:mysql://palmmysqldb:3306/palmdb";
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
    public mvSFM_QueryHistory() {
        properties = new Properties();

        try{
            Class.forName( classNameJdbcMysql );    //mysql
            Class.forName( classNameJdbcSnowflake );    //snowflake 2
        }catch (Exception e){
            e.printStackTrace();
        }
    }  // initiator

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
    } // setQueries
    public void setConnProperties() {
        //setting properties
        properties.put("user", sfUser);
        properties.put("password", sfPswd);
        properties.put("account", sfAccount); //account-id followed by cloud region.
        properties.put("warehouse", sfWarehouse);
        properties.put("db", sfDB);
        properties.put("schema", sfSchema);
        properties.put("role", sfRole);
        properties.put("insecureMode", "true");
    }

    public void getConnection() {
        try {
            sfConn = DriverManager.getConnection( sfjdbcUrl, properties);
            //System.out.println("\tJOE::Connection SF established, connection id : " + sfConn);

            //sfStmt = sfConn.createStatement();
            //sfStmt.executeQuery("ALTER SESSION SET JDBC_QUERY_RESULT_FORMAT='JSON'");
            //System.out.println("\tJOE::Alter Session > JSON statement, object-id : " + sfStmt);

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
            // remark for processQuery00
            //sfStmt.close();
            sfConn.close();

            mypStmt.close();
            // remark for processQuery00
            //myRS.close();
            //myStmt.close();
            myConn.close();

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void getProcessQuery00() {
        //System.out.println("\tJOE::selectSQL = " + sfselectSQL);
        //try-catch block
        try {
            sfStmt = sfConn.createStatement();
            //System.out.println("\tJoe::Got the SF statement object, object-id : " + sfStmt);

            sfRS = sfStmt.executeQuery(sfselectSQL);
            while(sfRS.next()) {
                //following rs.getXXX should also change as per your select query
                System.out.println("\tSnowflake CURRENT_VERSION(): " + sfRS.getString("FROMJAVA"));
            }
        } catch (SQLException exp) {
            exp.printStackTrace();
        }

        try {
            myStmt = myConn.createStatement();
            //System.out.println("\tJoe::Got the MYSQL statement object, object-id : " + myStmt);

            myRS = myStmt.executeQuery(myselectSQL);
            while(myRS.next()) {
                //following rs.getXXX should also change as per your select query
                System.out.println("\tmysql CURRENT_VERSION(): " + myRS.getString("fromJAVA"));
            }
        } catch (SQLException exp) {
            exp.printStackTrace();
        }
    }
    public void getProcessQuery01() {
        int progressMark = 0;
        int progressSuccessCount = 0;
        int firstProgressMark = 1;

        try {
            // make selectQueryString by arg case.
            sfpStmt = sfConn.prepareStatement(selectQueryHistory);
            sfpStmt.setTimestamp(1, selectFromTS );
            sfpStmt.setTimestamp(2, selectToTS );

            sfRS = sfpStmt.executeQuery();

            // make insertQueryString with ResultSet of select
            mypStmt = myConn.prepareStatement(insertQueryHistory);

            //System.out.println("\tJoe::Got the statement object, object-id : " + sfpStmt);
            System.out.println("::DB dump start: " + LocalDateTime.now() );

            while(sfRS.next()) {
                progressMark++;
                if ( progressMark == 10 || progressMark == 100 )
                    { firstProgressMark = progressMark; System.out.println(":" + progressMark);}
                if ( progressMark % firstProgressMark == 0) System.out.print(".");
                if ( progressMark % 1000 == 0) System.out.println(":" + progressMark);
                //following rs.getXXX should also change as per your select query

                /*
                System.out.print("\t>>getting Row is:");
                System.out.print("|" + sfRS.getString("QUERY_ID") );
                System.out.print("|" + sfRS.getTimestamp("START_TIME") );
                System.out.print("|" + sfRS.getString("USER_NAME") );
                System.out.print("|" + sfRS.getLong("SESSION_ID") );
                System.out.print("|" + sfRS.getInt("COMPILATION_TIME") );
                System.out.print("|" + sfRS.getTimestamp("NOWTS") );
                System.out.println();
                */

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

                    //LOG insert one row...
                    /*
                    System.out.println("\t\t>>End at Insert Row:"+sfRS.getString("QUERY_ID")+"|"+
                            sfRS.getTimestamp("START_TIME") +"|"+
                            sfRS.getBigDecimal("CREDITS_USED_CLOUD_SERVICES")
                    );
                    */
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
                }
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

    public int getArgs(String[] args) {
        /*
        System.out.println("<<arg Parsing start----: args.length:" + args.length );
        if ( args.length == 2 || args.length == 1 || args.length == 0 ) {

        } else {
            return (-1);
        }
        */
        switch(args.length){
            case 0: // minutes now -15, with default URL
                sfjdbcUrl = sfjdbcUrl00;
                tsRange = 15;
                selectFromTS = Timestamp.valueOf(makeTS( LocalDateTime.now() ).toLocalDateTime().plusMinutes(-tsRange) );
                selectToTS = makeTS( LocalDateTime.now() );
                flagDupProcess = false;
                break;
            case 1: // minutes now -15, with URL
                if ( Integer.parseInt( args[0] ) == 0 ) sfjdbcUrl = sfjdbcUrl00;
                else if ( Integer.parseInt( args[0] ) == 1 ) sfjdbcUrl = sfjdbcUrl01;
                else return(-1);
                tsRange = 15;
                selectFromTS = Timestamp.valueOf(makeTS( LocalDateTime.now() ).toLocalDateTime().plusMinutes(-tsRange) );
                selectToTS = makeTS( LocalDateTime.now() );
                flagDupProcess = false;
                break;
            case 2: // minutes now -m
                if ( Integer.parseInt( args[0] ) == 0 ) sfjdbcUrl = sfjdbcUrl00;
                else sfjdbcUrl = sfjdbcUrl01;
                try {
                    tsRange = Integer.parseInt( args[1] );
                    selectFromTS = Timestamp.valueOf(makeTS( LocalDateTime.now() ).toLocalDateTime().plusMinutes(-tsRange) );
                    selectToTS = makeTS( LocalDateTime.now() );
                    flagDupProcess = true;
                    break;
                } catch (Exception e) {
                    //System.out.println("JOE NUMNER FORMAT ERROR:" + e);
                    return(-1);
                }
            case 3:
                if ( Integer.parseInt( args[0] ) == 0 ) sfjdbcUrl = sfjdbcUrl00;
                else sfjdbcUrl = sfjdbcUrl01;
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

        mvSFM_QueryHistory my = new mvSFM_QueryHistory();

        if ( my.getArgs(args) == -1) {
            System.out.println("Usage_: mvSFM_QueryHistory { [0..1] { (minutes | Date Hour)} }");
            System.out.println("\tFirst 1 Arg is must Conn option: 0:Internet, 1:Private, other is this help.");
            System.out.println("\t2nd arg is 15 minutes for default, or arg 1 mean arg minutes, and break at DUP");
            System.out.println("\t2nd & 3rd Args are Date & Hour(0..23,24) args, 24 means hole day and not break at DUP");
        } else {
            my.setQueries();
            my.setConnProperties();
            my.getConnection();
            // my.getProcessQuery00();
            my.getProcessQuery01();
            my.closeConnection();
        } // args

        System.out.println("Oasis.snowflake.dump.2palm.end---"+ LocalDateTime.now());
    }
}