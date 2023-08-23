package joe.jvHello.javaPolyInterface;

public class testInterface {
    public static void main(String[] args) {
        RCU ar = new Radio();
        ar.chUp();
        ar.connInternet();

        RCU at = new TV();
        for (int i =0; i < 10; i++ ) at.chUp();
        at.connInternet();

        System.out.println(ar.toString());
        System.out.println(at.toString());
        System.out.println(at.hashCode());
        System.out.println(at.getClass());
    }
}
