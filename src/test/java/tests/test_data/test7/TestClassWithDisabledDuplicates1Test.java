package tests.test_data.test7;

import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class TestClassWithDisabledDuplicates1Test {
    @Test
    @TmsLink("1")
    @Disabled
    void t1() {
        System.out.println(1);
    }

    @Test
    @TmsLink("1")
    @Disabled
    void t2() {
        System.out.println(2);
    }
}
