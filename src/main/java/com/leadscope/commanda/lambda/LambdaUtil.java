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
