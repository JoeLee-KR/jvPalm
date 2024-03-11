package doitJava.doit_ch10_classpoly;

class Joe {
    int a=111;
    int b=333;
}

class Jenny {
    int a=111;
    int b=222;
    public String toString() {
        return ( "Jenny has (a,b) = (" + a + "," + b + ")" + super.toString() );
    }

    @Override
    public boolean equals(Object obj) {

        if (obj instanceof Jenny){
            System.out.println("Jenny Object: " + this.a +" and " + ((Jenny) obj).a );
            if( this.a == ((Jenny) obj).a )
                return true;
        }
        if (obj instanceof Joe) {
            System.out.println("Joe Object: " + this.a +" and " + ((Joe) obj).a );
            if( this.a == ((Joe) obj).a )
                return true;
        }
        //return super.equals(obj);
        return false;
    }
}

public class MethodOverriding06 {
    public static void main(String[] args) {
        Joe aa = new Joe();
        System.out.println(aa.hashCode());
        System.out.printf("%x\n", aa.hashCode());
        System.out.println(aa + "aa.a:" + aa.a );
        System.out.println(aa.toString());

        Jenny bb = new Jenny();
        Jenny bb2 = new Jenny();
        System.out.println(bb.hashCode());
        System.out.printf("%x\n", bb.hashCode());
        System.out.println(bb + "bb.a:" + bb.a );
        System.out.println(bb.toString());

        System.out.println("aa.equals(bb):" + aa.equals(bb) );
        System.out.println("bb.equals(aa):" + bb.equals(bb2) );
        System.out.println("bb.equals(aa):" + bb.equals(aa) );
    }
}

