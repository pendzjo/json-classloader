//"Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements; and to You under the Apache License, Version 2.0. "
package com.github.pendzjo.jsonclassloaderproject.testclasses.test12;

import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author johnnp
 */
public class TestClass12Tester {

    @Test
    public void basicTest() {
        int NUM_OF_CREATES = 10;

        for (int i = 0; i < NUM_OF_CREATES; i++) {
            TestClass12 cl = new TestClass12();
            Assert.assertEquals(i, cl.getInstationId());
        }

        Assert.assertEquals(NUM_OF_CREATES, TestClass12.getInstationCount());

    }

}
