/**
 * Copyright 2016 Leadscope, Inc. All rights reserved.
 * LEADSCOPE PROPRIETARY and CONFIDENTIAL. Use is subject to license terms.
 */
package com.leadscope.commanda.sources;

import org.apache.commons.csv.CSVFormat;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Registration of available commanda sources - this could be replaced with dynamic
 * registration via annotations and reflection
 */
public class CommandaSources {
  public final static LineSource defaultSource =
          new LineSource("utf8", "Text encoded in UTF-8", Charset.forName("UTF-8"));

  public final static List<? extends CommandaSource<?>> sources =
          Arrays.asList(
                  defaultSource,
                  new CSVSource("csv", "Default CSV format with UTF-8 encoding",
                          CSVFormat.DEFAULT, Charset.forName("UTF-8")),
                  new CSVSource("tab", "Tab-delimited CSV format with UTF-8 encoding",
                          CSVFormat.DEFAULT.withDelimiter('\t'), Charset.forName("UTF-8")),
                  new ToxmlSource()
          );

  public final static Map<String, CommandaSource<?>> sourceMap =
          sources.stream()
                  .collect(Collectors.toMap(s -> "-"+s.getArgName(), s -> s));

  public final static int maxArgLength() {
    return sources.stream()
            .mapToInt(s -> s.getArgName().length())
            .max().getAsInt();
  }

  public final static Optional<CommandaSource<?>> forArg(String arg) {
    return Optional.ofNullable(sourceMap.get(arg));
  }
}
