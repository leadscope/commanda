/**
 * Copyright 2016 Leadscope, Inc. All rights reserved.
 * LEADSCOPE PROPRIETARY and CONFIDENTIAL. Use is subject to license terms.
 */
package com.leadscope.commanda.lambda;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * The static and class imports that are included by default when compiling lambda
 * expressions.
 */
public class DefaultLambdaImports {
  public static final List<String> staticImports =
          Arrays.asList(
                  "java.util.stream.Stream.*",
                  "java.util.stream.Collectors.*",
                  "com.leadscope.commanda.lambda.LambdaUtil.*"
          );

  public static final List<Class<?>> imports =
          Arrays.asList(
                  Arrays.class,
                  Collections.class
          );
}
