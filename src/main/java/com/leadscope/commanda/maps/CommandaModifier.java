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

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A map that instead of returning a new value, modifies the value in place
 * @param <T> the type for both input and output
 */
public abstract class CommandaModifier<T> implements CommandaElementMap<T, T> {
  /**
   * Gets the consumer that will modify the value in memory
   * @return the consumer lambda
   */
  public abstract Consumer<T> getConsumer();

  @Override
  public Function<T, T> getFunction() {
    return new ModifiedIdentityMapFunction<>(getConsumer());
  }

  private static class ModifiedIdentityMapFunction<T> implements Function<T, T> {
    private Consumer<T> consumer;
    public ModifiedIdentityMapFunction(Consumer<T> consumer) {
      this.consumer = consumer;
    }

    @Override
    public T apply(T element) {
      consumer.accept(element);
      return element;
    }
  }
}
