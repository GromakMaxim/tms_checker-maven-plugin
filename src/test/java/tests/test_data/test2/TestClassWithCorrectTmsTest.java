package tests.test_data.test2;

import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class TestClassWithCorrectTmsTest {
    @Test
    @TmsLink("1")
    void t1() {
        System.out.println("template1");
    }

    @Test
    @TmsLink("2")
    public void t2() {
        System.out.println("template2");
    }

    @Test
    @TmsLink("3")
    @Disabled
    public void t3disabled() {
        System.out.println("template3");
    }
}
