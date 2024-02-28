package tests.test_templates.test1;

import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.Test;

public class TestClassWithDuplicateTms {
    @Test
    @TmsLink("1")
    void t1() {
        System.out.println("template1");
    }

    @Test
    @TmsLink("1")
    public void t2() {
        System.out.println("template2");
    }
}
