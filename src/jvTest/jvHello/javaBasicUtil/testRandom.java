package jvTest.jvHello.javaBasicUtil;
import java.util.*;

public class testRandom {
    public static void main(String[] args) {
        Random rnd = new Random();
        int i =0;
        while ( i++ < 10) {
            System.out.println( rnd.nextInt(10) );
        }
    }
}
