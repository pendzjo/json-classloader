//"Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements; and to You under the Apache License, Version 2.0. "
package com.github.pendzjo.jsonclassloader;

import com.github.pendzjo.jsonclassloader.helper.ClassLoaderDebug;
import com.github.pendzjo.jsonclassloader.helper.Undo;
import com.github.pendzjo.jsonclassloaderproject.testclasses.test1.TestClass1;
import com.github.pendzjo.jsonclassloaderproject.testclasses.test12.TestClass12;
import com.github.pendzjo.jsonclassloaderproject.testclasses.test2.TestClass2;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author johnnp
 */
public class JsonClassLoaderTest {

    @Test
    public void TestFilesPresent() {
        BasicTestObjectStruct.OBJECT_MAP.values().stream().map((btos) -> {
            assertTrue(String.format(
                    "File [%s] Path [%s] is not readable or exists there for tests will fail build test-classes subproject",
                    btos.simpleName, btos.jarPath),
                    Files.isReadable(btos.jarPath));
            return btos;
        }).forEachOrdered((btos) -> {
            assertTrue(String.format(
                    "File [%s] Path [%s] is not readable or exists there for tests will fail build test-classes subproject",
                    btos.simpleName, btos.jsonPath),
                    Files.isReadable(btos.jsonPath));
        });

    }

    @Test
    //Test to make sure current classes not loaded
    public void TestClassesNotLoaded() {

        try (Undo d = Undo.doThisWithEmptyClassPath()) {
            for (BasicTestObjectStruct btos : BasicTestObjectStruct.OBJECT_MAP.values()) {
                String className = btos.className;
                try {
                    ClassLoader p = Thread.currentThread().
                            getContextClassLoader();
                    p.loadClass(className);

                    ClassLoaderDebug.printLoadedClassLoader(p);

                    Assert.fail(String.format(
                            "%s is loaded in the classpath, that should be, from BasicTestObjectStruct [%s]",
                            className, btos.className));
                } catch (ClassNotFoundException ex) {
                }
            }
        }
    }

    //Turning provided paths into URLS
    private URL[] toURLArray(List<Path> paths) throws MalformedURLException {
        URL urls[] = new URL[paths.size()];
        int i = 0;
        for (Path p : paths) {
            urls[i++] = p.toUri().toURL();
        }
        return urls;
    }

    //Adding the BasicTester.java/.class file to the Path
    private URL[] addBasicTestURL(URL urls[]) throws MalformedURLException {
        URL newURLS[] = new URL[urls.length + 1];

        for (int i = 0; i < urls.length; i++) {
            newURLS[i] = urls[i];
        }
        Path p = Paths.get(
                BasicTestObjectStruct.STARTUP_PATH.toAbsolutePath().toString(),
                "target",
                "test-classes");
        assertTrue(Files.isReadable(p));
        newURLS[urls.length] = new URL(p.toUri().toURL().toString());
        return newURLS;

    }

    /**
     * Testing reading the basic TEST_CLASSES and making sure they load there
     * respective jars
     *
     * @throws MalformedURLException
     * @throws Exception
     */
    @Test
    public void testClassLoadBasic() throws MalformedURLException, Exception {

        try (Undo d = Undo.doThisWithEmptyClassPath()) {
            ClassLoader classloader = new URLClassLoader(
                    toURLArray(BasicTestObjectStruct.allJarPaths()));

            for (BasicTestObjectStruct btos : BasicTestObjectStruct.OBJECT_MAP.values()) {
                try {
                    Class testClass = classloader.loadClass(btos.className);
                    Object o = testClass.newInstance();
                } catch (Exception ex) {
                    Assert.fail(String.format("%s failed for some reason...",
                            btos.className));
                    throw ex;
                }
            }
        }
    }

    /**
     * Loading the basic test classes and making there there instationId() and
     * static counts are correct...confirming that they are indeed in differet
     * class paths...
     *
     * @throws java.net.MalformedURLException
     * @throws java.lang.ClassNotFoundException
     * @throws java.lang.InstantiationException
     * @throws java.lang.IllegalAccessException
     * @throws java.lang.NoSuchMethodException
     * @throws java.lang.NoSuchFieldException
     * @throws java.lang.reflect.InvocationTargetException
     */
    @Test
    public void testClassLoadBasicNew() throws MalformedURLException,
            ClassNotFoundException, InstantiationException,
            IllegalAccessException, NoSuchMethodException,
            IllegalArgumentException, InvocationTargetException,
            NoSuchFieldException {

        TestClass1 testClass1 = new TestClass1();
        assertEquals(TestClass1.getInstationCount() - 1, testClass1.
                getInstationId());
        TestClass2 testClass2 = new TestClass2();
        assertEquals(TestClass2.getInstationCount() - 1, testClass2.
                getInstationId());

        TestClass12 testClass12 = new TestClass12();
        assertEquals(TestClass12.getInstationCount() - 1, testClass12.
                getInstationId());

        assertEquals(TestClass1.getInstationCount() - 1, testClass12.
                getTestClass1().getInstationId());
        assertEquals(TestClass2.getInstationCount() - 1, testClass12.
                getTestClass2().getInstationId());

        assertEquals(testClass1.getClass(), testClass12.getTestClass1().
                getClass());

        //Custom classloader different memory space
        try (Undo d = Undo.doThis(addBasicTestURL(toURLArray(
                BasicTestObjectStruct.allJarPaths())),
                null)) {

            ClassLoaderDebug.printLoadedClassLoader(
                    Thread.currentThread().getContextClassLoader());

            Class basicTestClass =
                    Thread.currentThread().getContextClassLoader().loadClass(
                            "com.github.pendzjo.jsonclassloader.basic.BasicTester");

            Object bc = basicTestClass.cast(
                    basicTestClass.newInstance());
            Method m = basicTestClass.getMethod("test", null);
            Assert.assertTrue(Boolean.valueOf(String.valueOf(m.invoke(bc))));

            Field fcTestClass1 = basicTestClass.getDeclaredField("cTestClass1");
            Field fcTestClass2 = basicTestClass.getDeclaredField("cTestClass2");
            Field fcTestClass12 =
                    basicTestClass.getDeclaredField("cTestClass12");

            Assert.assertNotEquals(
                    "TestClass1 is equal...that shouldn't be, coming from different classloaders",
                    testClass1.getClass(), fcTestClass1.get(bc).getClass());

            Assert.assertNotEquals(
                    "TestClass2 is equal...that shouldn't be, coming from different classloaders",
                    testClass2.getClass(), fcTestClass2.get(bc).getClass());

            Assert.assertNotEquals(
                    "TestClass12 is equal...that shouldn't be, coming from different classloaders",
                    testClass12.getClass(), fcTestClass2.get(bc).getClass());

        }

        TestClass1 anotherTestClass1 = new TestClass1();
        assertEquals(TestClass1.getInstationCount() - 1, anotherTestClass1.
                getInstationId());

        assertEquals(testClass1.getClass(), anotherTestClass1.getClass());

    }

}
