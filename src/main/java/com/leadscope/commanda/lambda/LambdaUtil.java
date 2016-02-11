/**
 * Copyright 2016 Leadscope, Inc. All rights reserved.
 * LEADSCOPE PROPRIETARY and CONFIDENTIAL. Use is subject to license terms.
 */
package com.leadscope.commanda.lambda;

import pl.joegreen.lambdaFromString.TypeReference;

/**
 * Some utility methods that get statically imported for lambda code
 */
public class LambdaUtil {
  public static final TypeReference<String> STRING_TYPE = new TypeReference<String>(){};

  /**
   * @param v value to check
   * @return true iff v is null or only whitespace
   */
  public static boolean empty(String v) {
    return v == null || v.trim().length() == 0;
  }
}
