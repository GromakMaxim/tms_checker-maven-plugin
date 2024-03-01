package tests.test_data.test4;

import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.Test;

public class TestClassWithFewDuplicatesTest {
    @Test
    @TmsLink("111")
    public void t1(){
        System.out.println(1);
    }

    @Test
    @TmsLink("111")
    public void t2(){
        System.out.println(1);
    }

    @Test
    @TmsLink("222")
    public void t3(){
        System.out.println(1);
    }

    @Test
    @TmsLink("222")
    public void t4(){
        System.out.println(1);
    }
}
