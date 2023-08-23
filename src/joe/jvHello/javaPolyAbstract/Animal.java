package joe.jvHello.javaPolyAbstract;

public abstract class Animal {
    String name="...animal";
    public abstract void eat();
    public void move() {
        System.out.println("Move with 4legs" + name);
    }
}
