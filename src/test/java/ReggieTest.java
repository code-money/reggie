import org.junit.jupiter.api.Test;

/**
 * @program: reggie
 * @author: zyz
 * @create: 2022-07-21 15:52
 **/

public class ReggieTest {
    @Test
    void test1(){
        String fileName = "akdjfoiad.jpg";
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        System.out.println(suffix);
    }
}



