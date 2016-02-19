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

/**
 * The default commanda args used by the Commanda main command-line application
 */
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
