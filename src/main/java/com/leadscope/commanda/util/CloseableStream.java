/**
 * Copyright 2016 Leadscope, Inc. All rights reserved.
 * LEADSCOPE PROPRIETARY and CONFIDENTIAL. Use is subject to license terms.
 */
package com.leadscope.commanda.util;

import java.io.Closeable;
import java.util.stream.Stream;

/**
 * A wrapper containing a stream that is also closeable, and should be closed upon completion
 * @param <T>
 */
public interface CloseableStream<T> extends Closeable {
  Stream<T> getStream();
}
