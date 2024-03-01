package tests;

import org.example.FolderScanner;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class TmsCheckerTest {
    @Test
    @DisplayName("нет дубляжей - позитивный")
    public void testValidTestsExpectSuccess() {
        FolderScanner folderScanner = new FolderScanner("tests.test_templates.test2", true);

        assertDoesNotThrow(() -> folderScanner.findAllTestMethods(folderScanner.findAllClassesUsingClassLoader()));
    }

    @Test
    @DisplayName("есть дубляжи - должны упасть с сообщением")
    public void testDuplicateTmsLinksExpectExceptionWithMessage() {
        FolderScanner folderScanner = new FolderScanner("tests.test_templates.test1", true);
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
        FolderScanner folderScanner = new FolderScanner("tests.test_templates.test3", true);

        assertDoesNotThrow(() -> folderScanner.findAllTestMethods(folderScanner.findAllClassesUsingClassLoader()));
    }

    @Test
    @DisplayName("в классе несколько дубляжей - должны найти все и потом упасть")
    public void testWithFewDuplicates() {
        FolderScanner folderScanner = new FolderScanner("tests.test_templates.test4", false);
        assertThatThrownBy(() -> folderScanner.findAllTestMethods(folderScanner.findAllClassesUsingClassLoader()))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Found duplicate TmsLink! -> 111\nFound duplicate TmsLink! -> 222");
    }

    @Test
    @DisplayName("в классе несколько дубляжей - должны быстро падать при нахождении первого")
    public void testWithFewDuplicatesFailFast() {
        FolderScanner folderScanner = new FolderScanner("tests.test_templates.test4", true);
        assertThatThrownBy(() -> folderScanner.findAllTestMethods(folderScanner.findAllClassesUsingClassLoader()))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Found duplicate TmsLink! -> 111");
    }

    @Test
    @DisplayName("в классе дубляжи и они disabled - должны проверяться")
    public void testWithDisabledDuplicates_shouldBeChecked() {
        FolderScanner folderScanner = new FolderScanner("tests.test_templates.test5", false, false);
        assertThatThrownBy(() -> folderScanner.findAllTestMethods(folderScanner.findAllClassesUsingClassLoader()))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Found duplicate TmsLink! -> 1");
    }

    @Test
    @DisplayName("в классе дубляжи и они disabled - должны игнорироваться")
    public void testWithDisabledDuplicates_shouldBeIgnored() {
        FolderScanner folderScanner = new FolderScanner("tests.test_templates.test5", false, true);
        assertDoesNotThrow(() -> folderScanner.findAllTestMethods(folderScanner.findAllClassesUsingClassLoader()));
    }

}
