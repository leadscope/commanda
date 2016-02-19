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

import com.leadscope.commanda.CommandaArg;
import pl.joegreen.lambdaFromString.TypeReference;

/**
 * A named argument map from one type to another
 * @param <T> the input element type
 * @param <R> the output element type
 */
public interface CommandaMap<T, R> extends CommandaArg {
  /**
   * @return a reference to the input type
   */
  TypeReference<T> getInputType();

  /**
   * @return a reference to the output type
   */
  TypeReference<R> getOutputType();
}
