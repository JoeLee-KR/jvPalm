package jvTest.jvHello.javaPolyInterface;

public class Radio implements RCU {
    String name;
    public void chUp() {
        System.out.println("chUp-Radio");
    }
    public void chDown() {
        System.out.println("chDown-Radio");
    }
    public void volUp() {
        System.out.println("volUp-Radio");
    }
    public void volDown() {
        System.out.println("volDown-Radio");
    }
    public void connInternet() {
        System.out.println("NOT SUPPORT-Radio");
    }
}
