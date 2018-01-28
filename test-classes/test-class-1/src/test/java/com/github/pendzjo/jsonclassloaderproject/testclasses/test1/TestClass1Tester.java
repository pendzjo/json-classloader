//"Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements; and to You under the Apache License, Version 2.0. "
package com.github.pendzjo.jsonclassloaderproject.testclasses.test1;

import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author johnnp
 */
public class TestClass1Tester {

    @Test
    public void basicTest() {
        int NUM_OF_CREATES = 10;

        for (int i = 0; i < NUM_OF_CREATES; i++) {
            TestClass1 cl = new TestClass1();
            Assert.assertEquals(i, cl.getInstationId());
        }

        Assert.assertEquals(NUM_OF_CREATES, TestClass1.getInstationCount());

    }

}
