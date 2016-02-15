/**
 * Copyright 2016 Leadscope, Inc. All rights reserved.
 * LEADSCOPE PROPRIETARY and CONFIDENTIAL. Use is subject to license terms.
 */
package com.leadscope.commanda.lambda;

import java.util.ArrayList;
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

  /**
   * Creates a new list of imports starting with the defaults and then adding additional ones
   * @param additionalImports the import classes to add
   * @return a new list of imports
   */
  public static List<Class<?>> addImports(Class<?>... additionalImports) {
    List<Class<?>> newImports = new ArrayList<>(imports);
    newImports.addAll(Arrays.asList(additionalImports));
    return newImports;
  }

  /**
   * Creates a new list of static imports starting with the defaults and then adding additional ones
   * @param additionalImports the static imports to add
   * @return a new list of static imports
   */
  public static List<String> addStaticImports(String... additionalImports) {
    List<String> newImports = new ArrayList<>(staticImports);
    newImports.addAll(Arrays.asList(additionalImports));
    return newImports;
  }
}
