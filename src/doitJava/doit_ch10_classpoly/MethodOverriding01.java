package doitJava.doit_ch10_classpoly;

// Fruit <- Apple <- FineApple, Fruit <- Beat

class Fruit {
    public void color()  {
        System.out.println("color: U MUST OVERRIDE THIS METHOD.");
    }
}

class Apple extends Fruit {
    public void color() {
        System.out.println("color: RED");
    }
}

class FineApple extends Apple {
    public void color() {
        System.out.println("color: Yellow");
    }
}

class Beat extends Fruit {
    public void color() {
        System.out.println("color: Purple");
    }
}

public class MethodOverriding01 {
    public static void main(String[] args){
        Fruit f1 = new Fruit();
        Fruit f2 = new Beat();
        f1.color();
        f2.color();

        System.out.println("===== f3[]");
        Fruit[] f3 = new Fruit[] { new Fruit(), new Apple(),  new Beat() };
        for ( Fruit oneFruit: f3) {
            oneFruit.color();
        }

        System.out.println("===== polymorphism upcast");
        Apple a1 = new Apple();
        Fruit f4 = a1;
        a1.color();
        f4.color();   // allow, upcast, alloc Apple

        System.out.println("===== polymorphism downcast");
        Fruit f5 = (Beat) f2;   // f2 alloced Beat class
        f5.color();
        // Fruit f5_1 = (Beat) f1;    // cannot downcast, f1 is Fruit class
        // Beat f5_2 = (Beat) f1;    // cannot downcast, f1 is Fruit class

        Beat f6_1 = (Beat) f5;   // to re-downcast with same new Beat();
        f6_1.color();
        Fruit f6_2 = (Beat) f5;   // to re-downcast with same new Beat();
        f6_2.color();

        System.out.println("===== test instanceof");
        Fruit f7 = new Apple();
        Fruit f8 = new FineApple();
        f7.color();
        f8.color();

        System.out.println(f8 instanceof Fruit);
        System.out.println(f8 instanceof FineApple);
        System.out.println(f7 instanceof FineApple);
    }
}
