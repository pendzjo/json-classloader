//"Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements; and to You under the Apache License, Version 2.0. "
package com.github.pendzjo.jsonclassloaderproject.testclasses.test2;

/**
 *
 * Basic Test class
 *
 * @author johnnp
 */
public class TestClass2 {

    private static int INSTATION_COUNT = 0;

    private final int instationId;

    public TestClass2() {
        this.instationId = INSTATION_COUNT++;
    }

    public synchronized static int getInstationCount() {
        return INSTATION_COUNT;
    }

    public int getInstationId() {
        return instationId;
    }

    @Override
    public String toString() {
        return String.format(
                "%s - [instationId = %s] - [Classloader memory was %s], INSTATION_COUNT currently is %s ",
                super.toString(), instationId, this.getClass().getClassLoader(),
                getInstationCount());
    }
}
