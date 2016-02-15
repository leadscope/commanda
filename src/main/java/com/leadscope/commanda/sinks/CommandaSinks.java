/**
 * Copyright 2016 Leadscope, Inc. All rights reserved.
 * LEADSCOPE PROPRIETARY and CONFIDENTIAL. Use is subject to license terms.
 */
package com.leadscope.commanda.sinks;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class CommandaSinks {
  public final static List<CommandaSink<?>> sinks =
          Arrays.asList(
                  new ToxmlSink()
          );

  public final static Map<String, CommandaSink<?>> sinkMap =
          sinks.stream()
                  .collect(Collectors.toMap(s -> "-"+s.getArgName(), s -> s));

  public final static int maxArgLength() {
    return sinks.stream()
            .mapToInt(s -> s.getArgName().length())
            .max().getAsInt();
  }

  public final static Optional<CommandaSink<?>> forArg(String arg) {
    return Optional.ofNullable(sinkMap.get(arg));
  }
}
