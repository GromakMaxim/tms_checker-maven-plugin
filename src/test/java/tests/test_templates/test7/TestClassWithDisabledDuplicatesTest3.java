package tests.test_templates.test7;

import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class TestClassWithDisabledDuplicatesTest3 {
    @Test
    @TmsLink("3")
    @Disabled
    void t1() {
        System.out.println(3);
    }

    @Test
    @TmsLink("3")
    @Disabled
    void t2() {
        System.out.println(3);
    }
}

