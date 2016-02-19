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
package com.leadscope.commanda.maps;

import java.util.function.Function;

/**
 * Maps an input stream of type T to another stream of type R. The output stream is parallel
 * to the input type and each element is mapped
 * @param <T> the type of elements expected in the input stream
 * @param <R> the type of elements included in the output stream
 */
public interface CommandaElementMap<T, R> extends CommandaMap<T, R> {
  /**
   * Gets the function that will map the elements
   * @return the mapping function
   */
  Function<T, R> getFunction();
}
