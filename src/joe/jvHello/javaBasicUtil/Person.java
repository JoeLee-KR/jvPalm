package joe.jvHello.javaBasicUtil;

public class Person {
    private String pName;
    private int pAge;

    public Person(String name, int age){
        this.pName = name;
        this.pAge = age;
    }
    public String getName() { return pName;}
    public int getAge() { return pAge;}
}
