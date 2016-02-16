/**
 * Copyright 2016 Leadscope, Inc. All rights reserved.
 * LEADSCOPE PROPRIETARY and CONFIDENTIAL. Use is subject to license terms.
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
