package jvTest.jvHello;

class A{
    int m=3;
    int n=5;
    void abc(int m, int n){
        //m = this.m;
        this.m = m;
        n = n;
    }
}
public class shortTest {
    public static void main(String[] args)
    {
        System.out.println("Hello, shortTest...");
        A a = new A();
        a.abc(7,8);
        System.out.println(a.m);
        System.out.println(a.n);
    }
}
