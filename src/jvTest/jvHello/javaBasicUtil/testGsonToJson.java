package jvTest.jvHello.javaBasicUtil;
import com.google.gson.*;

public class testGsonToJson {
    public static void main(String[] args) {
        Person person = new Person("Joe", 53);
        Gson pJson = new Gson();
        String pJsonString = pJson.toJson( person );
        System.out.println( pJsonString );
    }
}
