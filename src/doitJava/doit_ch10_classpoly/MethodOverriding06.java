package doitJava.doit_ch10_classpoly;

class Joe {
    int a=111;
}

class Jenny extends Joe{
    int a=111;
    // int a=222; // for test
    int b=333;
    public String toString() {
        return ( "Jenny has (a,b) = (" + a + "," + b + ")" + super.toString() );
    }

    @Override
    public boolean equals(Object obj) {

        if (obj instanceof Jenny){
            System.out.println("  ==> Jenny Object in Jenny: " + this.a +" and " + ((Jenny) obj).a );
            if( this.a == ((Jenny) obj).a )
                return true;
        }
        if (obj instanceof Joe) {
            System.out.println("  ==> Joe Object in Jenny: " + this.a +" and " + ((Joe) obj).a );
            if( this.a == ((Joe) obj).a )
                return true;
        }
        //return super.equals(this);
        return false;
    }
}

public class MethodOverriding06 {
    public static void main(String[] args) {
        Joe aa = new Joe();
        System.out.printf("Decimal: %d, Hex: %x\n", aa.hashCode(), aa.hashCode() );
        System.out.println(aa            + ", and aa.a:" + aa.a );
        System.out.println(aa.toString() + ", and aa.a:" + aa.a);

        Jenny bb = new Jenny();
        Joe bb2 = new Jenny();

        System.out.println("==========");
        System.out.printf("%d, %x\n", bb.hashCode(), bb.hashCode());
        System.out.println(bb + ", and bb.a:" + bb.a );
        System.out.println(bb.toString());

        System.out.println("==========");
        System.out.println("<Joe>aa.equals(<Jenny>bb):" + aa.equals(bb) );
        System.out.println("==========");
        System.out.println("<Jenny>bb.equals(<Joe>aa):" + bb.equals(aa) );
        System.out.println("==========");
        System.out.println("<Jenny>bb.equals(<Jenny>bb2):" + bb.equals(bb2) );
        System.out.println("==========");
        System.out.println("<Jenny>bb2.equals(<Joe>aa):" + bb2.equals(aa) );
        System.out.println("==========");
        System.out.println("bb2.equals(aa):" + bb2.equals(aa) );
    }
}

