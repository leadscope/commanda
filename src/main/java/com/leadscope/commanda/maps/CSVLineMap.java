/**
 * Copyright 2016 Leadscope, Inc. All rights reserved.
 * LEADSCOPE PROPRIETARY and CONFIDENTIAL. Use is subject to license terms.
 */
package com.leadscope.commanda.maps;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import pl.joegreen.lambdaFromString.TypeReference;

import java.io.StringWriter;
import java.util.List;
import java.util.function.Function;

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
