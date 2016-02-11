/**
 * Copyright 2016 Leadscope, Inc. All rights reserved.
 * LEADSCOPE PROPRIETARY and CONFIDENTIAL. Use is subject to license terms.
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
