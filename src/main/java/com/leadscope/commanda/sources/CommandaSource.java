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
package com.leadscope.commanda.sources;

import com.leadscope.commanda.CommandaArg;
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
public interface CommandaSource<T> extends CommandaArg {
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
