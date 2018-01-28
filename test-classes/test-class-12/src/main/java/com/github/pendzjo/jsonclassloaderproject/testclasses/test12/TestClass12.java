//"Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements; and to You under the Apache License, Version 2.0. "
package com.github.pendzjo.jsonclassloaderproject.testclasses.test12;

import com.github.pendzjo.jsonclassloaderproject.testclasses.test1.TestClass1;
import com.github.pendzjo.jsonclassloaderproject.testclasses.test2.TestClass2;

/**
 *
 * Basic Test class
 *
 * @author johnnp
 */
public class TestClass12 {

    private static int INSTATION_COUNT = 0;

    private final int instationId;

    private final TestClass1 testClass1;

    private final TestClass2 testClass2;

    public TestClass12() {
        this.instationId = INSTATION_COUNT++;
        this.testClass1 = new TestClass1();
        this.testClass2 = new TestClass2();
    }

    public synchronized static int getInstationCount() {
        return INSTATION_COUNT;
    }

    public int getInstationId() {
        return instationId;
    }

    public TestClass1 getTestClass1() {
        return this.testClass1;
    }

    public TestClass2 getTestClass2() {
        return this.testClass2;
    }

    @Override
    public String toString() {
        return String.format(
                "%s - [instationId = %s] - [Classloader memory was %s], INSTATION_COUNT currently is %s [Testclass1 = %s], [Testclass2 = %s]",
                super.toString(), instationId, this.getClass().getClassLoader(),
                getInstationCount(), this.getTestClass1(), this.getTestClass2());
    }
}
