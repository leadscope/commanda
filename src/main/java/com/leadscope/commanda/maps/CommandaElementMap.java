/**
 * Copyright 2016 Leadscope, Inc. All rights reserved.
 * LEADSCOPE PROPRIETARY and CONFIDENTIAL. Use is subject to license terms.
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
