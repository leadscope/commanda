/**
 * Copyright 2016 Leadscope, Inc. All rights reserved.
 * LEADSCOPE PROPRIETARY and CONFIDENTIAL. Use is subject to license terms.
 */
package com.leadscope.commanda.maps;

import pl.joegreen.lambdaFromString.TypeReference;

/**
 * A named argument map from one type to another
 * @param <T> the input element type
 * @param <R> the output element type
 */
public interface CommandaMap<T, R> {
  /**
   * @return a reference to the input type
   */
  TypeReference<T> getInputType();

  /**
   * @return a reference to the output type
   */
  TypeReference<R> getOutputType();

  /**
   * Gets the name used on the command line, not including the hyphen. E.g. returning
   * "csv" would be used on the command line as -csv
   * @return the arg name
   */
  String getArgName();

  /**
   * Gets a brief description displayed in the usage message
   * @return a brief description
   */
  String getDescription();
}
