package jvTest.jvHello.javaPolyInterface;

public interface RCU {
    public static int MAXCH=99;
    public static int MINCH=1;
    public abstract void chUp();

    public abstract void chDown();

    public abstract void volUp();

    public abstract void volDown();

    public void connInternet() ;
}
