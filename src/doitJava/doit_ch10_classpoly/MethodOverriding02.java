package doitJava.doit_ch10_classpoly;

// 
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

class C extends B {
    public int m=2;
    public void print() {
        System.out.println("class C..." + m);
    }
}

public class MethodOverriding02 {
    public static void main(String[] args){
        System.out.println("===== upcast test");
        B bb1 = new B(); bb1.print();
        A aa1 = (A) bb1; aa1.print();

        C cc2 = new C(); cc2.print();
        B bb2 = (B) cc2; bb2.print();
        A aa2 = (A) cc2; aa2.print();

        System.out.println("===== downcast test");
        A a1 = new A(); a1.print();
        // B b1 = (B) a1; b1.print();  // Can not downcast
        A a2 = new B(); a2.print();
        ((A)a2).print();

        System.out.println("===== downcast test2");
        A a3 = ((A) new B()); a3.print();
        // A a4 = ((B) new A()); a4.print();  // can not downcast

        A a5 = ((A)a1); a5.print();
        A a6 = ((A)a2); a6.print();
        // B b2 = ((B) a1); b2.print();  // can not downcast
        B b3 = ((B) a2); b3.print();
        // C c2 = (C) a2; c2.print();  // Can not downcast

        System.out.println("===== downcast test3");
        A ax1 = new B(); ax1.print();
        A ax2 = new C(); ax2.print();
        ((B)ax2).print();
    }
}


