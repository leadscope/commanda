/**
 * Copyright 2016 Leadscope, Inc. All rights reserved.
 * LEADSCOPE PROPRIETARY and CONFIDENTIAL. Use is subject to license terms.
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
