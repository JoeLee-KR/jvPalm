package doitJava.doit_ch10_classpoly;

class AA {
    public int m=1;
    public void print() {
        System.out.println("class AA..." + m);
    }
}

class BB extends AA {
    public int m=3;
    public void print() {
        System.out.println("class BB..." + m);
    }
}

public class MethodOverriding03 {
    public static void main(String[] args) {
        AA aa = new AA(); aa.print();
        BB bb = new BB(); bb.print();

        System.out.println("=====");
        AA ab = new BB(); ab.print();
        ((AA)ab).print();
        ((BB)ab).print();
        AA ac = ((AA)ab); ac.print();

        System.out.println( (ab instanceof AA) + ":" + ((AA)ac instanceof AA) );
        System.out.println( (ab instanceof BB) + ":" + ((AA)ac instanceof BB) );

        System.out.println("=====");
        System.out.println(aa.m +":"+ bb.m +":"+ ab.m +":"+ ac.m);
        System.out.println(aa.m +":"+ bb.m +":"+ ((BB)ab).m +":"+ ((BB)ac).m);
    }
}

