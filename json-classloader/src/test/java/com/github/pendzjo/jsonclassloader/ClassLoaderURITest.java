//"Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements; and to You under the Apache License, Version 2.0. "
package com.github.pendzjo.jsonclassloader;

import com.github.pendzjo.jsonclassloader.helper.ClassLoaderDebug;
import com.github.pendzjo.jsonclassloader.helper.Undo;
import com.github.pendzjo.jsonclassloader.uri.ClassLoaderURI;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author johnnp
 */
public class ClassLoaderURITest {

    /**
     * Testing can load a file via the URI paths being created
     *
     * @throws URISyntaxException
     * @throws MalformedURLException
     */
    @Test
    public void BasicURITest() throws URISyntaxException, MalformedURLException {
        /* String uriString = "file://%s";

        ClassLoaderURI uris[] =
                new ClassLoaderURI[JsonClassLoaderTest.TEST_CLASSES.length];
        int i = 0;
        for (Path path : JsonClassLoaderTest.TEST_CLASSES) {
            String implementedURI = String.format(uriString, path.normalize());
            uris[i] = new ClassLoaderURI(implementedURI);
            i++;
        }


         */
        for (BasicTestObjectStruct btos : BasicTestObjectStruct.OBJECT_MAP.values()) {
            ClassLoaderURI cluri = new ClassLoaderURI(
                    btos.jarPath.toUri().toString());
            Assert.assertTrue(String.format("Jar file note Readable %s", cluri),
                    Files.isReadable(Paths.get(cluri.getURI())));
        }
    }

    /**
     * Loading the Basic Json files and making sure Basic Json Files work can be
     * created around them...
     *
     * @throws IOException
     */
    @Test
    public void loadBasicTestClasses() throws IOException {

        for (BasicTestObjectStruct btos : BasicTestObjectStruct.OBJECT_MAP.values()) {
            Path jsonFile = btos.jsonPath;
            Assert.assertTrue(String.format("File %s doesn't exist", jsonFile),
                    Files.isReadable(jsonFile));
            JsonClassLoader jcl =
                    JsonClassLoader.createJsonClassLoader(jsonFile);
            Assert.assertNotNull(String.format("Failure to load file %s",
                    jsonFile), jcl);
            System.out.printf("---- Testing %s --%n \t Parent %s %n",
                    jcl,
                    jcl.getParentURI());
            if (jcl.getParentURI() != null) {
                System.out.printf("Testing parent of %s %n", jcl);
                Assert.assertTrue(String.format(
                        "Parent file %s for %s doesn't exist",
                        jcl.getParentURI(), jsonFile),
                        Files.isReadable(Paths.get(jcl.getParentURI())));
            }

            for (ClassLoaderURI uri : jcl.getRawClasses()) {
                Assert.assertTrue(String.format(
                        "Classes file %s for %s doesn't exist", uri, jsonFile),
                        Files.isReadable(Paths.get(uri.getURI())));
            }
        }

    }

    @Test
    public void testBasicFactory() {

        for (BasicTestObjectStruct btos : BasicTestObjectStruct.OBJECT_MAP.values()) {
            System.out.println(
                    "-------------------------------------------------------------");
            ClassLoader classLoader = null;
            try { //SOMETHING NOT WORKING....
                classLoader = MemoryClassLoader.produce(
                        btos.jsonPath);
                try (Undo u = Undo.doThis(classLoader)) {
                    System.out.printf("Final class for %s is %s %n",
                            btos.simpleName,
                            classLoader);
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
                org.junit.Assert.fail(String.format("Error on btos named %s",
                        btos.simpleName));
            }
        }

    }

    @Test
    public void testBasicParentCreation() throws MalformedURLException,
            ClassNotFoundException,
            InstantiationException, IllegalAccessException {

        BasicTestObjectStruct btos = BasicTestObjectStruct.OBJECT_MAP.get(
                BasicTestObjectStruct.TEST_CLASS12_KEY);

        JsonClassLoader jcl =
                JsonClassLoader.createJsonClassLoader(
                        btos.jsonPath);

        System.out.printf("JCL == %s %n", jcl);

        JsonClassLoader others[] = jcl.getClassLoaders();

        URLClassLoader parent = new URLClassLoader(new URL[0],
                null);

        for (JsonClassLoader other : others) {
            System.out.printf("Other JCLs %s %n", other);
            parent = new URLClassLoader(other.getClasses(), parent);
        }

        URLClassLoader use = new URLClassLoader(jcl.getClasses(), parent);

        ClassLoaderDebug.printLoadedClassLoader(use);

        try (Undo u = Undo.doThis(use);) {

            Class clazz = use.loadClass(btos.className);
            System.out.printf("Class found %s with clazz name %s %n", clazz,
                    btos.className);

            Object o = clazz.newInstance();
            System.out.printf("Object is %s %n", o);

        }
    }

    @Test
    public void testBasicMemoryClassLoaderWithParentKey() throws
            MalformedURLException,
            ClassNotFoundException, InstantiationException,
            IllegalAccessException {

        BasicTestObjectStruct btos = BasicTestObjectStruct.OBJECT_MAP.get(
                BasicTestObjectStruct.TEST_PARENT_KEY);

        JsonClassLoader jcl =
                JsonClassLoader.createJsonClassLoader(
                        btos.jsonPath);

        System.out.printf("JCL == %s %n", jcl);

        ClassLoader use = MemoryClassLoader.produce(jcl);

        ClassLoaderDebug.printLoadedClassLoader(use);

        try (Undo u = Undo.doThis(use);) {

            Class clazz = use.loadClass(btos.className);
            System.out.printf("Class found %s with clazz name %s %n", clazz,
                    btos.className);

            Object o = clazz.newInstance();
            System.out.printf("Object is %s %n", o);

        }

    }

}
