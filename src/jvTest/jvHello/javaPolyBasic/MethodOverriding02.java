package jvTest.jvHello.javaPolyBasic;

class A {
    public int m=0;
    public void print() {
        System.out.println("class A..." + m);
    }
}

class B extends A {
    public int m=1;
    public void print() {
        System.out.println("class B..." + m);
    }
}

public class MethodOverriding02 {
    public static void main(String[] args){
        A aa = new A(); aa.print();
        B bb = new B(); bb.print();

        System.out.println("=====");
        A ab = new B(); ab.print();
        ((A)ab).print();
        A ac = ((A)ab); ac.print();

        System.out.println( ab instanceof A);
        System.out.println( (A)ac instanceof A);

    }
}
