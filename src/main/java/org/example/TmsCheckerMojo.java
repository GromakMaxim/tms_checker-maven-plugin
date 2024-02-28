package org.example;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * проверка дублирующихся @TmsLink в указанной папке с тестами
 */
@Mojo(name = "tms_checker-maven-plugin", defaultPhase = LifecyclePhase.COMPILE)
public class TmsCheckerMojo extends AbstractMojo {

    /**
     * папка верхнего уровня, внутри которой располагаются все тестовые методы (с аннотацией @Test)
     */
    @Parameter(property = "testsFolder", required = true)
    private String testsFolder;

    /**
     * при обнаружении ошибки блокируем дальнейшее выполнение, падаем с ошибкой
     */
    @Parameter(property = "isFailFast", defaultValue = "true")
    private Boolean isFailFast;

    @Override
    public void execute() throws MojoExecutionException {
        
    }

}
