/**
 * Commanda - command-line lambdas for Java
 * Copyright (C) 2016 Scott Miller - Leadscope, Inc.
 *
 * Leadscope, Inc. licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
