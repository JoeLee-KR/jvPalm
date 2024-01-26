package jvTest.jvHello.doit_ch10_classpoly;

class AAA {
    static public int m=1;
    static void print() {
        System.out.println("class AAA with static print()..." + m);
    }
}

class BBB extends AAA {
    static public int m=3;
    public static void print() {
        System.out.println("class BBB with static print()..." + m);
    }
}

public class MethodOverriding04 {
    public static void main(String[] args) {
        AAA.m = 10;
        BBB.m = 30;
        AAA aa = new AAA(); System.out.println("aa.m=" + aa.m + ",AAA.m=" + AAA.m);
        BBB bb = new BBB(); System.out.println("bb.m=" + bb.m + ",BBB.m=" + BBB.m);
        bb.m = 44;          System.out.println("bb.m=" + bb.m + ",BBB.m=" + BBB.m);
        AAA ab = new BBB(); System.out.println("ab.m=" + ab.m + ",AAA.m=" + AAA.m + ",BBB.m=" + BBB.m);
        ab.m = 33;          System.out.println("ab.m=" + ab.m + ",AAA.m=" + AAA.m + ",BBB.m=" + BBB.m);

        System.out.println("=====");
        AAA.print();
        BBB.print();
        aa.print();
        bb.print();
        ab.print();
    }
}

