/**
 * Copyright 2016 Leadscope, Inc. All rights reserved.
 * LEADSCOPE PROPRIETARY and CONFIDENTIAL. Use is subject to license terms.
 */
package com.leadscope.commanda.sources;

import com.leadscope.commanda.util.CloseableStream;
import pl.joegreen.lambdaFromString.TypeReference;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Stream;

/**
 * Provides a stream of type T from either a list of files or input stream
 * @param <T> the type of output stream
 */
public interface CommandaSource<T> {
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

  /**
   * Creates a stream that will read from the given input stream
   * @param is the input stream
   * @return the stream that will pull from the given input
   */
  Stream<T> stream(InputStream is);

  /**
   * Creates a stream that will read from the given files.
   * @param files the input files
   * @return the stream that will pull from the given files
   */
  CloseableStream<T> stream(List<File> files);

  /**
   * @return a reference to the type of element
   */
  TypeReference<T> getElementType();
}
