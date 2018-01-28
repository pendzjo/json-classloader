//"Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements; and to You under the Apache License, Version 2.0. "
package com.github.pendzjo.jsonclassloader;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author johnnp
 */
public class BasicTestObjectStruct {

    public final String simpleName;

    public final String className;

    public final Path jarPath;

    public final Path jsonPath;

    private BasicTestObjectStruct(String simpleName, String className,
            Path jarPath, Path jsonPath) {
        this.simpleName = simpleName;
        this.className = className;
        this.jarPath = jarPath;
        this.jsonPath = jsonPath;
    }

    public static final String TEST_CLASS1_KEY = "test-class1";
    public static final String TEST_CLASS2_KEY = "test-class2";
    public static final String TEST_CLASS12_KEY = "test-class12";

    public static final String TEST_PARENT_KEY = "parent-class12";
    public static final Path STARTUP_PATH = Paths.get("");

    public static final Map<String, BasicTestObjectStruct> OBJECT_MAP =
            new HashMap<>();

    static {
        List<BasicTestObjectStruct> structList = new ArrayList<>();

        structList.add(new BasicTestObjectStruct(TEST_CLASS1_KEY,
                "com.github.pendzjo.jsonclassloaderproject.testclasses.test1.TestClass1",
                Paths.get(STARTUP_PATH.
                        toAbsolutePath().toString(), "..", "test-classes",
                        "test-class-1",
                        "target", "test-class-1-1.0-SNAPSHOT.jar"),
                Paths.get(STARTUP_PATH.
                        toAbsolutePath().toString(), "..", "test-classes",
                        "test-class-1",
                        "src", "test", "resources", "basic-test-class-1.json")));

        structList.add(new BasicTestObjectStruct(TEST_CLASS2_KEY,
                "com.github.pendzjo.jsonclassloaderproject.testclasses.test2.TestClass2",
                Paths.get(STARTUP_PATH.
                        toAbsolutePath().toString(), "..", "test-classes",
                        "test-class-2",
                        "target", "test-class-2-1.0-SNAPSHOT.jar"),
                Paths.get(STARTUP_PATH.
                        toAbsolutePath().toString(), "..", "test-classes",
                        "test-class-2",
                        "src", "test", "resources", "basic-test-class-2.json")));

        structList.add(new BasicTestObjectStruct(TEST_CLASS12_KEY,
                "com.github.pendzjo.jsonclassloaderproject.testclasses.test12.TestClass12",
                Paths.get(STARTUP_PATH.
                        toAbsolutePath().toString(), "..", "test-classes",
                        "test-class-12",
                        "target", "test-class-12-1.0-SNAPSHOT.jar"),
                Paths.get(STARTUP_PATH.
                        toAbsolutePath().toString(), "..", "test-classes",
                        "test-class-12",
                        "src", "test", "resources", "basic-test-class-12.json")));

        structList.add(new BasicTestObjectStruct(TEST_PARENT_KEY,
                "com.github.pendzjo.jsonclassloaderproject.testclasses.test12.TestClass12",
                Paths.get(STARTUP_PATH.
                        toAbsolutePath().toString(), "..", "test-classes",
                        "test-class-12",
                        "target", "test-class-12-1.0-SNAPSHOT.jar"),
                Paths.get(STARTUP_PATH.
                        toAbsolutePath().toString(), "..", "test-classes",
                        "test-class-12",
                        "src", "test", "resources", "parents",
                        "parent-test-class-12.json")));

        structList.forEach((btos) -> {
            OBJECT_MAP.put(btos.simpleName, btos);
        });

    }

    public static List<Path> allJarPaths() {
        List<Path> plist = new ArrayList<>();

        OBJECT_MAP.values().forEach(v -> {
            plist.add(v.jarPath);
        });
        return plist;
    }

    public static List<Path> allJsonPaths() {
        List<Path> plist = new ArrayList<>();

        OBJECT_MAP.values().forEach(v -> {
            plist.add(v.jsonPath);
        });
        return plist;
    }

}
