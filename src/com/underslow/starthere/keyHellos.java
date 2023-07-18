package com.underslow.starthere;

import java.time.*;
import java.sql.*;
//import jargs.gnu

public class keyHellos {
    Timestamp selectFromTS;
    Timestamp selectToTS;
    int tsRange;

    public void testTime1() {
        System.out.println("<<");
        //LocalDateTime nowDT;
        LocalDateTime startDT = LocalDateTime.now();
        LocalDate startD = LocalDate.now();
        System.out.println("\tLocalDateTime:" + startDT +"|"+ startD);

        //String date = "2023-07-07 01:01:01"; 이것은 parse 오류이다.
        String sdatetime = "2023-07-02T01:02:03";
        LocalDateTime ldatetime = LocalDateTime.parse(sdatetime);
        System.out.println("\tString sdatetime:" + sdatetime + "|LocalDateTime:" + ldatetime + "|" + ldatetime.plusDays(1) );

        String sdate = "2023-07-07";
        LocalDateTime ldatetime2 =  LocalDate.parse(sdate).atTime(4,5,6);
        System.out.println("\tString sdate:" + sdate + "|LocalDate:" + ldatetime2 + "|" + ldatetime2.plusHours(1) );

        // java.LocalDateTime과 sql.Timestamp 혼용
        Timestamp ts = Timestamp.valueOf( startDT.plusDays(1) );
        System.out.println("\tSQL Timestamp of LocalTime NOW:" + ts +"|at TS>Ltime>plusMethod(ts):" +
                Timestamp.valueOf( ts.toLocalDateTime().plusDays(1) ) );
        System.out.println(">>");
    }
    public void testTime2(){
        System.out.println("<<");
        // LocalDateTime NOW_DateTIme와 주어진 Static DateTime을 기준으로
        // from TS, to TS를 만들어내어 본다. (w/offset)

        // foldering custom block...
        LocalDateTime staticDT = LocalDateTime.now();
        LocalDate staticD = staticDT.toLocalDate();
        Timestamp fromTS = Timestamp.valueOf( staticD.atTime(11,12,13) ); // good
        Timestamp toTS = Timestamp.valueOf( staticDT );
        System.out.println("\tTSd+Local: " + staticDT +"|"+ staticD +"|"+ fromTS +"|");
        System.out.println("\tTSt+Local: " + staticDT +"|"+ staticD +"|"+ toTS +"|");
        System.out.println("\t\t+1 hour: " + fromTS.toLocalDateTime().plusHours(1) +"|");
        System.out.println("\t\t+1 day: " + fromTS.toLocalDateTime().plusDays(1) +"|");
        //System.out.println("\t\tR+1 hour: " + fromTS.toLocalDateTime() +"|"+ fromTS.toLocalDateTime().plusHours(1));

        staticDT = LocalDateTime.parse("2000-01-02T03:04:05.6789") ;
        staticD = staticDT.toLocalDate();
        fromTS = Timestamp.valueOf( staticD.atTime(0,0,0) ); // good
        System.out.println("\tTS&Local: " + staticDT +"|"+ staticD +"|"+ fromTS +"|");

        System.out.println(">>");
    }
    public Timestamp makeTS(String strDate){
        return Timestamp.valueOf( LocalDate.parse(strDate).atTime(0,0,0) );
    }
    public Timestamp makeTS(LocalDateTime staticDT){
        return Timestamp.valueOf( staticDT );
    }
    public void testTime3(){
        System.out.println("$strDate > TS  :" +
                makeTS( "1970-01-02" ) +"|"+
                Timestamp.valueOf(makeTS( "1970-01-02" ).toLocalDateTime().plusDays(1) ) +"|"+
                Timestamp.valueOf(makeTS( "1970-01-02" ).toLocalDateTime().plusHours(7) ) +"|"+
                Timestamp.valueOf(makeTS( "1970-01-02" ).toLocalDateTime().plusHours(7+1) ) +"|"
        );

        System.out.println("$localTime > TS:" +
                makeTS( LocalDateTime.now() ) +"|" +
                Timestamp.valueOf(makeTS( LocalDateTime.now() ).toLocalDateTime().plusDays(-1) ) +"|"+
                Timestamp.valueOf(makeTS( LocalDateTime.now() ).toLocalDateTime().plusMinutes(-15) ) +"|"
        );
    }

    public void testMark(int lastMark) {
        System.out.println("<<Progress Mark start----:" + lastMark );
        int progressMark = 1;
        for( ; progressMark < lastMark; progressMark++ ) {
            try {
                Thread.sleep(20);
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
            if ( progressMark % 10 == 0) System.out.print(".");
            if ( progressMark % 100 == 0) System.out.println(":"+ progressMark );
        }
        System.out.println(":"+ progressMark);
        System.out.println(">>Progress Mark end----:" );
    }

    public int testArgs(String[] args) {
        /*
        System.out.println("<<arg Parsing start----: args.length:" + args.length );
        if ( args.length == 2 || args.length == 1 || args.length == 0 ) {

        } else {
            return (-1);
        }
        */
        switch(args.length){
            case 0: // minutes now -15
                tsRange = 15;
                selectFromTS = Timestamp.valueOf(makeTS( LocalDateTime.now() ).toLocalDateTime().plusMinutes(-tsRange) );
                selectToTS = makeTS( LocalDateTime.now() );
                break;
            case 1: // minutes now -m
                try {
                    tsRange = Integer.parseInt( args[0] );
                    selectFromTS = Timestamp.valueOf(makeTS( LocalDateTime.now() ).toLocalDateTime().plusMinutes(-tsRange) );
                    selectToTS = makeTS( LocalDateTime.now() );
                    break;
                } catch (Exception e) {
                    //System.out.println("JOE NUMNER FORMAT ERROR:" + e);
                    return(-1);
                }
            case 2:
                try {
                    tsRange = Integer.parseInt( args[1] );
                    if (tsRange<24) {   // range 1 hour
                        selectFromTS = Timestamp.valueOf(makeTS( args[0] ).toLocalDateTime().plusHours(tsRange) );
                        selectToTS = Timestamp.valueOf(makeTS( args[0] ).toLocalDateTime().plusHours(tsRange+1) );
                    } else {            // range 24 hour, 1day
                        selectFromTS = makeTS( args[0] );
                        selectToTS = Timestamp.valueOf(makeTS( args[0] ).toLocalDateTime().plusDays(1) );
                    }
                    break;
                } catch (Exception e) {
                    //System.out.println("JOE:" + e);
                    return(-1);
                }
            default:
                return (-1);
        }
        System.out.println("\ttsRange: "+ tsRange);
        System.out.println("\tselectFromTS: " + selectFromTS );
        System.out.println("\tselectToTS:   " + selectToTS );
        System.out.println(">>arg Parsing end----: sDate:" );
        return (0);
    }

    public void testTypes() {
        long tmpLong = 123456789;
        //toString()
        System.out.println("<<testType" + tmpLong);
    }

    public static void main(String[] args) {
        keyHellos hello = new keyHellos();
        //hello.testTime1();
        //hello.testTime2();
        //hello.testTime3();
        //hello.testMark(149);
        if ( hello.testArgs(args) == -1) {
            System.out.println("dUsage: Command {minutes} | {Date Hour}");
            System.out.println("\tnull args is 15 minutes for default, or 1 args minutes and break at DUP");
            System.out.println("\tDate Hour(0..23,24), 24 means hole day and not break at DUP");
        }
        //hello.testTypes();
    }
}
