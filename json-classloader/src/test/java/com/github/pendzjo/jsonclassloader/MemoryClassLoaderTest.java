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
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 * @author johnnp
 */
public class MemoryClassLoaderTest {

    /**
     * Test creation of a ClassLoader via the MemoryClassLoader
     *
     * @throws MalformedURLException
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    @Test
    public void testBasicMemoryClassLoaderWithParentKey() throws
            MalformedURLException,
            ClassNotFoundException, InstantiationException,
            IllegalAccessException {
        try (MemoryClassLoader memoryClassLoader = new MemoryClassLoader()) {
            BasicTestObjectStruct btos = BasicTestObjectStruct.OBJECT_MAP.get(
                    BasicTestObjectStruct.TEST_PARENT_KEY);

            JsonClassLoader jcl =
                    JsonClassLoader.createJsonClassLoader(
                            btos.jsonPath);

            ClassLoader use = memoryClassLoader.produce(jcl);

            //ClassLoaderDebug.printLoadedClassLoader(use);
            try (Undo u = Undo.doThis(use);) {

                Class clazz = use.loadClass(btos.className);

                Object o = clazz.newInstance();

            }
        }

    }

    /**
     * Loading all the BasicTestObjectStruct via the MemoryClassLoader...
     */
    @Test
    public void testBasicFactory() {

        try (MemoryClassLoader memoryClassLoader = new MemoryClassLoader();) {

            for (BasicTestObjectStruct btos : BasicTestObjectStruct.OBJECT_MAP.values()) {

                ClassLoader classLoader = null;
                try { //SOMETHING NOT WORKING....
                    classLoader = memoryClassLoader.produce(
                            btos.jsonPath);
                    try (Undo u = Undo.doThis(classLoader)) {
                        Class testClass = classLoader.loadClass(btos.className);
                        Object o = testClass.newInstance();
                    }
                } catch (Throwable ex) {
                    ex.printStackTrace();
                    System.out.printf(
                            "************* testBasicFactory failed with classloader %s ************** %n",
                            classLoader);
                    ClassLoaderDebug.printLoadedClassLoader(classLoader);
                    System.out.printf(
                            "*************************************************** %n");
                    org.junit.Assert.fail(
                            String.format("Error on btos named %s",
                                    btos.simpleName));
                }
            }
        }
    }

    /**
     * Loading the basic test classes and making there there instationId() and
     * static counts are correct...confirming that they are indeed in different
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
        try (MemoryClassLoader memoryClassLoader = new MemoryClassLoader();) {
            /**
             * All These classes get created by the Junit Classloader
             */
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
            BasicTestObjectStruct parentLoader =
                    BasicTestObjectStruct.OBJECT_MAP.get(
                            BasicTestObjectStruct.TEST_PARENT_KEY);

            JsonClassLoader parent = JsonClassLoader.createJsonClassLoader(
                    parentLoader.jsonPath);
            JsonClassLoader tester = mockJsonClassLoaderForTest(parent);

            ClassLoader classLoader = memoryClassLoader.produce(tester);

            //ClassLoaderDebug.printLoadedClassLoader(classLoader);
            try (Undo d = Undo.doThis(classLoader)) {

                ClassLoaderDebug.printLoadedClassLoader(
                        Thread.currentThread().getContextClassLoader());

                Class basicTestClass =
                        Thread.currentThread().getContextClassLoader().loadClass(
                                "com.github.pendzjo.jsonclassloader.basic.BasicTester");

                Object bc = basicTestClass.cast(
                        basicTestClass.newInstance());
                Method m = basicTestClass.getMethod("test", null);
                Assert.assertTrue(Boolean.valueOf(String.valueOf(m.invoke(bc))));

                Field fcTestClass1 = basicTestClass.getDeclaredField(
                        "cTestClass1");
                Field fcTestClass2 = basicTestClass.getDeclaredField(
                        "cTestClass2");
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
                        testClass12.getClass(), fcTestClass12.get(bc).getClass());

            }

            /**
             * Back to the orginal Junit memory classloader
             */
            TestClass1 anotherTestClass1 = new TestClass1();
            assertEquals(TestClass1.getInstationCount() - 1, anotherTestClass1.
                    getInstationId());

            assertEquals(testClass1.getClass(), anotherTestClass1.getClass());

            /**
             * Ok loading up this json file to a new classpath...
             */
            BasicTestObjectStruct test1 = BasicTestObjectStruct.OBJECT_MAP.get(
                    BasicTestObjectStruct.TEST_CLASS1_KEY);

            ClassLoader test1ClassLoader = memoryClassLoader.produce(
                    test1.jsonPath);

            try (Undo u = Undo.doThis(test1ClassLoader)) {
                Class test1Class =
                        Thread.currentThread().getContextClassLoader().loadClass(
                                test1.className);
                Object o = test1Class.newInstance();
                Method instationId = test1Class.getMethod("getInstationId");
                Object getInstationId = instationId.invoke(o);
                assertEquals(0, getInstationId);
            }

            test1ClassLoader = memoryClassLoader.produce(test1.jsonPath);

            try (Undo u = Undo.doThis(test1ClassLoader)) {
                Class test1Class =
                        Thread.currentThread().getContextClassLoader().loadClass(
                                test1.className);
                Object o = test1Class.newInstance();
                Method instationId = test1Class.getMethod("getInstationId");
                Object getInstationId = instationId.invoke(o);
                assertEquals(1, getInstationId);
            }

            test1ClassLoader = memoryClassLoader.produce(test1.jsonPath);

            try (Undo u = Undo.doThis(test1ClassLoader)) {
                Class test1Class =
                        Thread.currentThread().getContextClassLoader().loadClass(
                                test1.className);
                Object o = test1Class.newInstance();
                Method instationId = test1Class.getMethod("getInstationId");
                Object getInstationId = instationId.invoke(o);
                assertEquals(2, getInstationId);
            }

        }
    }

    public JsonClassLoader mockJsonClassLoaderForTest(JsonClassLoader parent)
            throws MalformedURLException {
        Path toClass = Paths.get(
                BasicTestObjectStruct.STARTUP_PATH.toAbsolutePath().toString(),
                "target",
                "test-classes");
        JsonClassLoader mock = mock(JsonClassLoader.class);
        when(mock.getClasses()).thenReturn(new URL[]{new URL(
            toClass.toUri().toURL().toString())});

        when(mock.getParent()).thenReturn(parent);

        when(mock.getClassLoaders()).thenReturn(new JsonClassLoader[0]);

        when(mock.getFileLocation()).thenReturn(Paths.get(""));

        return mock;
    }

}
