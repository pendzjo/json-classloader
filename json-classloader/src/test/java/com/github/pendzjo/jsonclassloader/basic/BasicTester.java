//"Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements; and to You under the Apache License, Version 2.0. "
package com.github.pendzjo.jsonclassloader.basic;

import com.github.pendzjo.jsonclassloaderproject.testclasses.test1.TestClass1;
import com.github.pendzjo.jsonclassloaderproject.testclasses.test12.TestClass12;
import com.github.pendzjo.jsonclassloaderproject.testclasses.test2.TestClass2;

/**
 *
 * @author johnnp
 */
public class BasicTester {

    public TestClass1 cTestClass1;
    public TestClass2 cTestClass2;
    public TestClass12 cTestClass12;

    public BasicTester() {

    }

    public boolean test() {
        cTestClass1 = new TestClass1();
        if (cTestClass1.getInstationId() != 0) {
            return false;
        }
        cTestClass2 = new TestClass2();
        if (cTestClass2.getInstationId() != 0) {
            return false;
        }

        cTestClass12 = new TestClass12();
        if (cTestClass12.getInstationId() != 0) {
            return false;
        }
        if (cTestClass12.getTestClass1().getInstationId() != 1) {
            return false;
        }
        if (cTestClass12.getTestClass2().getInstationId() != 1) {
            return false;
        }
        return true;
    }

}
