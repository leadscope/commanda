/**
 * Copyright 2016 Leadscope, Inc. All rights reserved.
 * LEADSCOPE PROPRIETARY and CONFIDENTIAL. Use is subject to license terms.
 */
package com.leadscope.commanda.sources;

import com.leadscope.commanda.util.CloseableStream;
import com.leadscope.commanda.util.FileEnumeration;
import com.leadscope.commanda.util.InputStreamStream;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import pl.joegreen.lambdaFromString.TypeReference;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * A source that reads comma-separated-value records from the input
 */
public class CSVSource implements CommandaSource<CSVRecord> {
  public static final TypeReference<CSVRecord> CSV_TYPE =
          new TypeReference<CSVRecord>(){};

  private String argName;
  private String description;
  private CSVFormat format;
  private Charset charset;

  /**
   * @param argName the name for the command-line argument
   * @param description a description that should mention the format
   * @param format csv format to use
   * @param charset the charset encoding of the input
   */
  public CSVSource(String argName, String description, CSVFormat format, Charset charset) {
    this.argName = argName;
    this.description = description;
    this.format = format;
    this.charset = charset;
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
  public CloseableStream<CSVRecord> stream(List<File> files) {
    InputStream is = FileEnumeration.concatFiles(files);
    Stream<CSVRecord> stream = stream(is);
    return new InputStreamStream<>(is, stream);
  }

  @Override
  public Stream<CSVRecord> stream(InputStream inputStream) {
    try {
      CSVParser parser = new CSVParser(new InputStreamReader(inputStream, charset), format);
      return StreamSupport.stream(parser.spliterator(), false);
    }
    catch (RuntimeException re) {
      throw re;
    }
    catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public TypeReference<CSVRecord> getElementType() {
    return CSV_TYPE;
  }
}
