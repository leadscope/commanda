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
import java.util.stream.Stream;

/**
 * Maps an input stream of type T to another stream of type R. The new stream is not
 * necessarily parallel to the input stream and may provide a different number of objects.
 * @param <T> the input stream type
 * @param <R> the output stream type
 */
public interface CommandaStreamMap<T, R> extends CommandaMap<T, R> {
  /**
   * Gets the function that will map the elements
   * @return the mapping function
   */
  Function<Stream<T>, Stream<R>> getFunction();
}
