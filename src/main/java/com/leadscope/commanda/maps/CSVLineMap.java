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
package com.leadscope.commanda.maps;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import pl.joegreen.lambdaFromString.TypeReference;

import java.io.StringWriter;
import java.util.List;
import java.util.function.Function;

/**
 * Maps lists of strings into CSV record strings based on the given format
 */
public class CSVLineMap implements CommandaElementMap<List<String>, String>, Function<List<String>, String> {
  private String argName;
  private String description;
  private CSVFormat noBreakFormat;

  /**
   * @param argName the name for the command-line argument
   * @param description a description that should mention the format
   * @param format csv format to use
   */
  public CSVLineMap(String argName, String description, CSVFormat format) {
    this.argName = argName;
    this.description = description;
    this.noBreakFormat = format.withRecordSeparator(null);
  }

  @Override
  public TypeReference<List<String>> getInputType() {
    return new TypeReference<List<String>>(){};
  }

  @Override
  public TypeReference<String> getOutputType() {
    return new TypeReference<String>(){};
  }

  @Override
  public String getArgName() {
    return argName;
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public Function<List<String>, String> getFunction() {
    return this;
  }

  @Override
  public String apply(List<String> element) {
    StringWriter sw = new StringWriter();
    try {
      CSVPrinter printer = new CSVPrinter(sw, noBreakFormat);
      printer.printRecord(element);
      printer.close();
      return sw.toString();
    }
    catch (RuntimeException re) {
      throw re;
    }
    catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
