/**
 * Copyright 2016 Leadscope, Inc. All rights reserved.
 * LEADSCOPE PROPRIETARY and CONFIDENTIAL. Use is subject to license terms.
 */
package com.leadscope.commanda.maps;

import com.leadscope.commanda.lambda.DefaultLambdaImports;
import org.apache.commons.csv.CSVFormat;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.leadscope.commanda.lambda.LambdaUtil.STRING_TYPE;

/**
 * The commanda maps available to the application
 */
public class CommandaMaps {
  public final static List<CommandaMap<?, ?>> maps =
          Arrays.asList(
                  new LambdaElementMap(DefaultLambdaImports.imports, DefaultLambdaImports.staticImports,
                          "s->s", STRING_TYPE, STRING_TYPE),
                  new LambdaStreamMap(DefaultLambdaImports.imports, DefaultLambdaImports.staticImports,
                          "in->in", STRING_TYPE, STRING_TYPE),
                  new LambdaModifier(DefaultLambdaImports.imports, DefaultLambdaImports.staticImports,
                          "s->{}", STRING_TYPE),
                  new CSVLineMap("csvlines", "Maps lists of strings into CSV lines",
                          CSVFormat.DEFAULT),
                  new CSVLineMap("tablines", "Maps lists of strings into tab-delimited lines",
                          CSVFormat.DEFAULT.withDelimiter('\t'))
          );

  public final static Map<String, CommandaMap<?, ?>> mapMap =
          maps.stream()
                  .collect(Collectors.toMap(m -> "-"+m.getArgName(), m -> m));

  public final static int maxArgLength() {
    return maps.stream()
            .mapToInt(s -> s.getArgName().length())
            .max().getAsInt();
  }

  public final static Optional<CommandaMap<?, ?>> forArg(String arg) {
    return Optional.ofNullable(mapMap.get(arg));
  }
}
