package doitJava.doit_ch10_classpoly;

class AAAA {
    AAAA(int a) {
        System.out.println("Creator by AAAA...with:" + a);
    }
}

class BBBB extends AAAA {
    BBBB() {
        super(1);
    }
    BBBB(int b) { super (b); }
}

public class MethodOverriding05 {
    public static void main(String[] args) {
        AAAA a = new AAAA(5);
        BBBB b = new BBBB();
        BBBB b2 = new BBBB(55);
    }
}

