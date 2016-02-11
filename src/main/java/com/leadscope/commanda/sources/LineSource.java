/**
 * Copyright 2016 Leadscope, Inc. All rights reserved.
 * LEADSCOPE PROPRIETARY and CONFIDENTIAL. Use is subject to license terms.
 */
package com.leadscope.commanda.sources;

import com.leadscope.commanda.lambda.LambdaUtil;
import com.leadscope.commanda.util.CloseableStream;
import com.leadscope.commanda.util.FileEnumeration;
import com.leadscope.commanda.util.InputStreamStream;
import pl.joegreen.lambdaFromString.TypeReference;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;
import java.util.stream.Stream;

/**
 * A source that parses the input as character encoded lines
 */
public class LineSource implements CommandaSource<String> {

  private String argName;
  private String description;
  private Charset charset;

  /**
   * @param argName the name for the command-line argument
   * @param description a description that should mention the format
   * @param charset the charset encoding of the input
   */
  public LineSource(String argName, String description, Charset charset) {
    this.argName = argName;
    this.description = description;
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
  public CloseableStream<String> stream(List<File> files) {
    InputStream is = FileEnumeration.concatFiles(files);
    Stream<String> stream = stream(is);
    return new InputStreamStream<>(is, stream);
  }

  @Override
  public Stream<String> stream(InputStream inputStream) {
    try {
      InputStreamReader isr = new InputStreamReader(inputStream, charset);
      BufferedReader br = new BufferedReader(isr);
      return br.lines();
    }
    catch (RuntimeException re) {
      throw re;
    }
    catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public TypeReference<String> getElementType() {
    return LambdaUtil.STRING_TYPE;
  }
}
