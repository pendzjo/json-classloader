//"Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements; and to You under the Apache License, Version 2.0. "
package com.github.pendzjo.jsonclassloader.helper;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * Class to handle the Undoing of ClassLoader loading utilizing autocloseable...
 *
 * @author johnnp
 */
public class Undo implements AutoCloseable {

    private final ClassLoader backTo;

    public static synchronized Undo doThis(ClassLoader newClassLoader) {
        ClassLoader current = Thread.currentThread().getContextClassLoader();
        Undo ret = new Undo(current);
        Thread.currentThread().setContextClassLoader(newClassLoader);
        return ret;
    }

    public static synchronized Undo doThisWithEmptyClassPath() {
        return doThis(new URL[0], null);
    }

    public static synchronized Undo doThis(URL[] urls, ClassLoader parent) {
        ClassLoader loader = new URLClassLoader(urls, parent);
        return doThis(loader);
    }

    private Undo(ClassLoader backTo) {
        this.backTo = backTo;
    }

    @Override
    public synchronized void close() {
        Thread.currentThread().setContextClassLoader(backTo);
    }

}
