/**
 * Copyright 2016 Leadscope, Inc. All rights reserved.
 * LEADSCOPE PROPRIETARY and CONFIDENTIAL. Use is subject to license terms.
 */
package com.leadscope.commanda.sinks;

import com.leadscope.commanda.CommandaArg;
import pl.joegreen.lambdaFromString.TypeReference;

import java.io.OutputStream;
import java.util.stream.Stream;

/**
 * A sink at the end of a commanda chain that writes the results
 * to a provided OutputStream
 * @param <T>
 */
public interface CommandaSink<T> extends CommandaArg {
  /**
   * @return a reference to the input type
   */
  TypeReference<T> getInputType();

  void consume(Stream<T> input, OutputStream output);
}
