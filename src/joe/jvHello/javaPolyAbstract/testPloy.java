package joe.jvHello.javaPolyAbstract;

public class testPloy {
    public static void main(String[] args) {

        Animal ani = new Cat();
        ani.eat();
        ani.move();
        //((Cat)ani).night();


        Animal adog = new Dog();
        adog.eat();
        adog.move();
    }
}
