/**
 * Copyright 2016 Leadscope, Inc. All rights reserved.
 * LEADSCOPE PROPRIETARY and CONFIDENTIAL. Use is subject to license terms.
 */
package com.leadscope.commanda;

import com.leadscope.commanda.lambda.DefaultLambdaImports;
import com.leadscope.commanda.maps.CSVLineMap;
import com.leadscope.commanda.maps.LambdaElementMap;
import com.leadscope.commanda.maps.LambdaModifier;
import com.leadscope.commanda.maps.LambdaStreamMap;
import com.leadscope.commanda.sinks.ToxmlSink;
import com.leadscope.commanda.sources.CSVSource;
import com.leadscope.commanda.sources.LineSource;
import com.leadscope.commanda.sources.ToxmlSource;
import org.apache.commons.csv.CSVFormat;

import java.nio.charset.Charset;
import java.util.Arrays;

import static com.leadscope.commanda.lambda.LambdaUtil.STRING_TYPE;

public class DefaultCommandaArgs {
  public final static LineSource defaultSource =
          new LineSource("utf8", "Text encoded in UTF-8", Charset.forName("UTF-8"));

  public static CommandaArgs defaultArgs() {
    return new CommandaArgs(
            defaultSource,
            Arrays.asList(
                    defaultSource,
                    new CSVSource("csv", "Default CSV format with UTF-8 encoding",
                            CSVFormat.DEFAULT, Charset.forName("UTF-8")),
                    new CSVSource("tab", "Tab-delimited CSV format with UTF-8 encoding",
                            CSVFormat.DEFAULT.withDelimiter('\t'), Charset.forName("UTF-8")),
                    new ToxmlSource()
            ),
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
            ),
            Arrays.asList(
                    new ToxmlSink()
            )
    );
  }
}
