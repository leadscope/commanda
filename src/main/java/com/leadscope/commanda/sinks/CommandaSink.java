/**
 * Copyright 2016 Leadscope, Inc. All rights reserved.
 * LEADSCOPE PROPRIETARY and CONFIDENTIAL. Use is subject to license terms.
 */
package com.leadscope.commanda.sinks;

import pl.joegreen.lambdaFromString.TypeReference;

import java.io.OutputStream;
import java.util.stream.Stream;

/**
 * A sink at the end of a commanda chain that writes the results
 * to a provided OutputStream
 * @param <T>
 */
public interface CommandaSink<T> {
  /**
   * @return a reference to the input type
   */
  TypeReference<T> getInputType();

  void consume(Stream<T> input, OutputStream output);

  /**
   * Gets the name used on the command line, not including the hyphen. E.g. returning
   * "toxOut" would be used on the command line as -toxOut
   * @return the arg name
   */
  String getArgName();

  /**
   * Gets a brief description displayed in the usage message
   * @return a brief description
   */
  String getDescription();
}
