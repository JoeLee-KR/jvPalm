package jvTest.jvHello.javaPolyInterface;

public class TV implements RCU{
    private int currentCh=95;
    public void chUp() {
        if( ++currentCh > RCU.MAXCH ) currentCh = MINCH;
        System.out.println("chUp-TV" + currentCh);
    }
    public void chDown() {
        if ( --currentCh < RCU.MINCH ) currentCh = MAXCH;
        System.out.println("chDown-TV" + currentCh);
    }
    public void volUp() {
        System.out.println("volUp-TV");
    }
    public void volDown() {
        System.out.println("volDown-TV");
    }
    public void connInternet() {
        System.out.println("SmartTV");
    }
    public String toString() {
        return( "[" + super.toString() + "] this is TV" );
    }
}
