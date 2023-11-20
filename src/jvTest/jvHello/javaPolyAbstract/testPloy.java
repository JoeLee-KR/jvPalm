package jvTest.jvHello.javaPolyAbstract;

public class testPloy {
    public static void main(String[] args) {

        Animal ani = new Cat();
        ani.eat();
        ani.move();
        ((Animal)ani).move();
        //((Cat)ani).night();

        Cat cat = new Cat();
        cat.move();
        ((Animal)cat).move();

        Animal adog = new Dog();
        adog.eat();
        adog.move();
        ((Animal)adog).move();
    }
}
