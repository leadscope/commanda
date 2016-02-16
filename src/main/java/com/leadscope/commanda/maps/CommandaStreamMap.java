/**
 * Copyright 2016 Leadscope, Inc. All rights reserved.
 * LEADSCOPE PROPRIETARY and CONFIDENTIAL. Use is subject to license terms.
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
