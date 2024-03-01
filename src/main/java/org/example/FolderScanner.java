package org.example;

import io.qameta.allure.TmsLink;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toSet;


public class FolderScanner {
    private final String packageName;
    private final boolean isFailFast;
    private final boolean ignoreDisabledTests;

    public FolderScanner(String packageName, boolean isFailFast) {
        this.packageName = packageName;
        this.isFailFast = isFailFast;
        this.ignoreDisabledTests = false;
    }

    public FolderScanner(String packageName, boolean isFailFast, boolean ignoreDisabledTests) {
        this.packageName = packageName;
        this.isFailFast = isFailFast;
        this.ignoreDisabledTests = ignoreDisabledTests;
    }

    public Set<Class> findAllClassesUsingClassLoader() {
        InputStream stream = ClassLoader.getSystemClassLoader()
                .getResourceAsStream(packageName.replaceAll("[.]", "/"));

        if (stream == null) throw new RuntimeException("Cant find folder: " + packageName);

        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        return reader.lines()
                .filter(line -> line.endsWith(".class"))
                .map(line -> getClass(line, packageName))
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
                    //.peek(m-> System.out.println(Arrays.toString(m.getDeclaredAnnotations())))
                    .filter(filterByTmsLinks)
                    .filter(method -> !ignoreDisabledTests || !filterByDisabled.test(method))
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

    private final Predicate<Method> filterByTmsLinks = method -> method.isAnnotationPresent(TmsLink.class);
    private final Predicate<Method> filterByDisabled = method -> Arrays.stream(method.getDeclaredAnnotations()).filter(a->a.toString().contains("Disabled(")).toList().size() != 0;
}
