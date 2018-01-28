//"Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements; and to You under the Apache License, Version 2.0. "
package com.github.pendzjo.jsonclassloader.helper;

import java.net.URL;
import java.net.URLClassLoader;

/**
 *
 * @author johnnp
 */
public class ClassLoaderDebug {

    /**
     * Print the loaded classfiles in the classpath
     *
     * @param classpath
     */
    public static void printLoadedClassLoader(ClassLoader classpath) {
        ClassLoader p = classpath;
        while (p != null) {
            if (p instanceof URLClassLoader) {
                URLClassLoader tmp = (URLClassLoader) p;
                for (URL u : tmp.getURLs()) {
                    System.out.printf("%s - %s %n", u, tmp);
                }
            } else {
                System.out.printf("ClassLoader %s - can't get list %n", p);
            }
            p = p.getParent();
        }
    }

}
