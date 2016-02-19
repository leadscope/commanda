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
package com.leadscope.commanda.sources;

import com.leadscope.commanda.util.CloseableStream;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import pl.joegreen.lambdaFromString.TypeReference;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
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
    if (files.size() == 0) {
      throw new IllegalArgumentException("No files were provided to CSV source");
    }
    return new MultipleFileStream(files);
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

  private class MultipleFileStream implements CloseableStream<CSVRecord> {
    private List<InputStream> inputStreams = new ArrayList<>();
    private Stream<CSVRecord> stream;
    public MultipleFileStream(List<File> files) {
      for (File file : files) {
        if (!file.exists()) {
          throw new RuntimeException("File does not exist: " + file.getAbsolutePath());
        }
        if (!file.canRead()) {
          throw new RuntimeException("File cannot be read: " + file.getAbsolutePath());
        }
      }

      try {
        for (File file : files) {
          FileInputStream fis = new FileInputStream(file);
          inputStreams.add(fis);
          if (stream == null) {
            stream = stream(fis);
          }
          else {
            stream = Stream.concat(stream, stream(fis));
          }
        }
      }
      catch (RuntimeException re) {
        close();
        throw re;
      }
      catch (Exception e) {
        close();
        throw new RuntimeException(e);
      }
      catch (Throwable t) {
        close();
        throw t;
      }
    }

    public void close() {
      for (InputStream is : inputStreams) {
        try {
          is.close();
        }
        catch (Throwable t) { }
      }
    }

    @Override
    public Stream<CSVRecord> getStream() {
      return stream;
    }
  }
}
