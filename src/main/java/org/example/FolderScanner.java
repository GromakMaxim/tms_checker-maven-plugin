package org.example;

import io.qameta.allure.TmsLink;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.*;

import static java.util.stream.Collectors.toSet;


public class FolderScanner {
    private final String PACKAGE_NAME;
    private boolean isFailFast;

    public FolderScanner(String PACKAGE_NAME, boolean isFailFast) {
        this.PACKAGE_NAME = PACKAGE_NAME;
        this.isFailFast = isFailFast;
    }

    public Set<Class> findAllClassesUsingClassLoader() {
        InputStream stream = ClassLoader.getSystemClassLoader()
                .getResourceAsStream(PACKAGE_NAME.replaceAll("[.]", "/"));

        if (stream == null) throw new RuntimeException("Cant find folder: " + PACKAGE_NAME);

        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        return reader.lines()
                .filter(line -> line.endsWith(".class"))
                .map(line -> getClass(line, PACKAGE_NAME))
                .collect(toSet());
    }

    private Class getClass(String className, String packageName) {
        try {
            return Class.forName(packageName + "." + className.substring(0, className.lastIndexOf('.')));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void findAllTestMethods(Set<Class> classesSet) {
        Set<String> tmsLinks = new HashSet<>();
        Set<String> duplicatedTmsLinks = new HashSet<>();

        StringBuilder sb = new StringBuilder();

        List<Method> annotatedMethods = new ArrayList<>();

        for (Class clazz : classesSet) {
            var methods = clazz.getDeclaredMethods();

            Arrays.stream(methods)
                    .filter(method -> method.isAnnotationPresent(TmsLink.class))
                    .map(method -> method.getAnnotation(TmsLink.class).value())
                    .forEach(tms -> {
                        if (!tmsLinks.contains(tms)) {
                            tmsLinks.add(tms);
                        } else {
                            sb.append("Found duplicate TmsLink! -> " + tms + "\n");
                            if (!duplicatedTmsLinks.contains(tms)) duplicatedTmsLinks.add(tms);
                            if (isFailFast) throw new RuntimeException(sb.toString().substring(0, sb.toString().length()-1));
                        }
                    });

        }

        if (!duplicatedTmsLinks.isEmpty() || !sb.isEmpty()) throw new RuntimeException(sb.toString().substring(0, sb.toString().length()-1));

    }
}
