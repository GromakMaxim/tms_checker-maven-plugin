package tests;

import org.example.FolderScanner;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class TmsCheckerTest {
    @Test
    @DisplayName("нет дубляжей - позитивный")
    public void testValidTestsExpectSuccess() {
        FolderScanner folderScanner = new FolderScanner("tests.test_data.test2", true);

        assertDoesNotThrow(() -> folderScanner.findAllTestMethods(folderScanner.findAllClassesUsingClassLoader()));
    }

    @Test
    @DisplayName("есть дубляжи - должны упасть с сообщением")
    public void testDuplicateTmsLinksExpectExceptionWithMessage() {
        FolderScanner folderScanner = new FolderScanner("tests.test_data.test1", true);
        assertThatThrownBy(() -> folderScanner.findAllTestMethods(folderScanner.findAllClassesUsingClassLoader()))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Found duplicate TmsLink! -> 1");
    }

    @Test
    @DisplayName("передаётся несуществующая директория - должны упасть с сообщением")
    public void testWithWrongFolder() {
        FolderScanner folderScanner = new FolderScanner("tests.blablabla.andblaagain", true);

        assertThatThrownBy(() -> folderScanner.findAllTestMethods(folderScanner.findAllClassesUsingClassLoader()))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Cant find folder: tests.blablabla.andblaagain");
    }

    @Test
    @DisplayName("передаётся пустой класс - не должно быть падений")
    public void testWithEmptyClass() {
        FolderScanner folderScanner = new FolderScanner("tests.test_data.test3", true);

        assertDoesNotThrow(() -> folderScanner.findAllTestMethods(folderScanner.findAllClassesUsingClassLoader()));
    }

    @Test
    @DisplayName("в классе несколько дубляжей - должны найти все и потом упасть")
    public void testWithFewDuplicates() {
        FolderScanner folderScanner = new FolderScanner("tests.test_data.test4", false);
        assertThatThrownBy(() -> folderScanner.findAllTestMethods(folderScanner.findAllClassesUsingClassLoader()))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Found duplicate TmsLink! -> 111\nFound duplicate TmsLink! -> 222");
    }

    @Test
    @DisplayName("в классе несколько дубляжей - должны быстро падать при нахождении первого")
    public void testWithFewDuplicatesFailFast() {
        FolderScanner folderScanner = new FolderScanner("tests.test_data.test4", true);
        assertThatThrownBy(() -> folderScanner.findAllTestMethods(folderScanner.findAllClassesUsingClassLoader()))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Found duplicate TmsLink! -> 111");
    }

    @Test
    @DisplayName("в классе дубляжи и они disabled - должны проверяться")
    public void testWithDisabledDuplicates_shouldBeChecked() {
        FolderScanner folderScanner = new FolderScanner("tests.test_data.test5", false, false);
        assertThatThrownBy(() -> folderScanner.findAllTestMethods(folderScanner.findAllClassesUsingClassLoader()))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Found duplicate TmsLink! -> 1");
    }

    @Test
    @DisplayName("в классе дубляжи и они disabled - должны игнорироваться")
    public void testWithDisabledDuplicates_shouldBeIgnored() {
        FolderScanner folderScanner = new FolderScanner("tests.test_data.test5", false, true);
        assertDoesNotThrow(() -> folderScanner.findAllTestMethods(folderScanner.findAllClassesUsingClassLoader()));
    }

    @Test
    @DisplayName("передаётся несколько классов(есть дубляжи, есть disabled, есть пустой) - игнорируем disabled, падаем при первом дубляже")
    public void testComplex1() {
        FolderScanner folderScanner = new FolderScanner("tests.test_data.test6", true, true);
        assertThatThrownBy(() -> folderScanner.findAllTestMethods(folderScanner.findAllClassesUsingClassLoader()))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Found duplicate TmsLink! -> 111");
    }

    @Test
    @DisplayName("передаётся несколько классов(есть дубляжи, есть disabled, есть пустой) - игнорируем disabled, ищем все дубляжи")
    public void testComplex2() {
        FolderScanner folderScanner = new FolderScanner("tests.test_data.test6", false, true);
        assertThatThrownBy(() -> folderScanner.findAllTestMethods(folderScanner.findAllClassesUsingClassLoader()))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Found duplicate TmsLink! -> 111\nFound duplicate TmsLink! -> 222");
    }

    @Test
    @DisplayName("передаётся несколько классов(есть дубляжи, есть disabled, есть пустой) - вкл disabled, падаем при первом дубляже")
    public void testComplex3() {
        FolderScanner folderScanner = new FolderScanner("tests.test_data.test6", true, false);
        assertThatThrownBy(() -> folderScanner.findAllTestMethods(folderScanner.findAllClassesUsingClassLoader()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageMatching("^Found duplicate TmsLink! -> \\d{1,10}$");
    }

    @Test
    @DisplayName("передаётся несколько классов(есть дубляжи, есть disabled, есть пустой) - вкл disabled, ищем все дубляжи")
    public void testComplex4() {
        FolderScanner folderScanner = new FolderScanner("tests.test_data.test6", false, false);
        assertThatThrownBy(() -> folderScanner.findAllTestMethods(folderScanner.findAllClassesUsingClassLoader()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageMatching("^Found duplicate TmsLink! -> \\d{1,10}\\nFound duplicate TmsLink! -> \\d{1,10}\\nFound duplicate TmsLink! -> \\d{1,10}$");
    }

    @Test
    @DisplayName("передаётся несколько классов(только дубляжи, только disabled) - вкл disabled, ищем все дубляжи")
    public void testComplex5() {
        FolderScanner folderScanner = new FolderScanner("tests.test_data.test7", false, false);
        assertThatThrownBy(() -> folderScanner.findAllTestMethods(folderScanner.findAllClassesUsingClassLoader()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageMatching("^Found duplicate TmsLink! -> \\d{1,10}\\nFound duplicate TmsLink! -> \\d{1,10}\\nFound duplicate TmsLink! -> \\d{1,10}$");
    }

    @Test
    @DisplayName("передаётся несколько классов(только дубляжи, только disabled) - вкл disabled, ищем первый дубляж")
    public void testComplex6() {
        FolderScanner folderScanner = new FolderScanner("tests.test_data.test7", true, false);
        assertThatThrownBy(() -> folderScanner.findAllTestMethods(folderScanner.findAllClassesUsingClassLoader()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageMatching("^Found duplicate TmsLink! -> \\d{1,10}$");
    }

    @Test
    @DisplayName("передаётся несколько классов(только дубляжи, только disabled) - игнорируем disabled, не должен падать")
    public void testComplex7() {
        FolderScanner folderScanner = new FolderScanner("tests.test_data.test7", false, true);
        assertDoesNotThrow(() -> folderScanner.findAllTestMethods(folderScanner.findAllClassesUsingClassLoader()));
    }

    @Test
    @DisplayName("передаётся несколько классов(только дубляжи, только disabled) - игнорируем disabled, failFast=true, не должен падать")
    public void testComplex8() {
        FolderScanner folderScanner = new FolderScanner("tests.test_data.test7", true, true);
        assertDoesNotThrow(() -> folderScanner.findAllTestMethods(folderScanner.findAllClassesUsingClassLoader()));
    }

}
