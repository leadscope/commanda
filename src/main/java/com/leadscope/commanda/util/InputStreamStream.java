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
package com.leadscope.commanda.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Stream;

/**
 * Simple wrapper for a closeable input stream
 * @param <T> the type of element in the stream
 */
public class InputStreamStream<T> implements CloseableStream<T> {
  private final InputStream is;
  private final Stream<T> stream;

  public InputStreamStream(InputStream is, Stream<T> stream) {
    this.is = is;
    this.stream = stream;
  }

  public Stream<T> getStream() {
    return stream;
  }

  public void close() throws IOException {
    is.close();
  }
}
