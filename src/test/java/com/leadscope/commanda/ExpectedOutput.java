/**
 * Copyright 2016 Leadscope, Inc. All rights reserved.
 * LEADSCOPE PROPRIETARY and CONFIDENTIAL. Use is subject to license terms.
 */
package com.leadscope.commanda;

import org.junit.Assert;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.function.Consumer;

/**
 * Throws assertion error if the values are not matched exactly
 */
public class ExpectedOutput implements Consumer<String> {
  private String msg;
  private LinkedList<String> values;

  public ExpectedOutput(String msg, String... values) {
    this.msg = msg;
    this.values = new LinkedList<>(Arrays.asList(values));
  }

  public void assertAllFound() {
    if (!values.isEmpty()) {
      Assert.fail(msg + " - still expecting " + values.getFirst());
    }
  }

  @Override
  public void accept(String s) {
    if (values.isEmpty()) {
      Assert.fail(msg + " - no longer expecting values saw: " + s);
    }
    Assert.assertEquals(msg + " - expected a different value", values.pop(), s);
  }
}
