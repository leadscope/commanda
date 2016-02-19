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
import com.leadscope.stucco.io.ToxmlErrorHandler;
import com.leadscope.stucco.io.ToxmlFileSource;
import com.leadscope.stucco.io.ToxmlParser;
import com.leadscope.stucco.model.CompoundRecord;
import pl.joegreen.lambdaFromString.TypeReference;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * A source that streams CompoundRecord objects from ToxML documents
 */
public class ToxmlSource implements CommandaSource<CompoundRecord> {
  public static final TypeReference<CompoundRecord> COMPOUND_RECORD_TYPE =
          new TypeReference<CompoundRecord>(){};

  @Override
  public String getArgName() {
    return "tox";
  }

  @Override
  public String getDescription() {
    return "Parses ToxML input";
  }

  @Override
  public CloseableStream<CompoundRecord> stream(List<File> files) {
    return new ToxmlIterators(files);
  }

  @Override
  public Stream<CompoundRecord> stream(InputStream inputStream) {
    try {
      ToxmlStreamIterator iter = new ToxmlStreamIterator(inputStream);
      return StreamSupport.stream(Spliterators.spliteratorUnknownSize(
              iter, Spliterator.ORDERED | Spliterator.NONNULL), false);
    }
    catch (RuntimeException re) {
      throw re;
    }
    catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public TypeReference<CompoundRecord> getElementType() {
    return COMPOUND_RECORD_TYPE;
  }

  private static class ToxmlStreamIterator implements Iterator<CompoundRecord> {
    private CompoundRecord nextObj;
    private XMLStreamReader reader;

    public ToxmlStreamIterator(InputStream is) throws Exception {
      reader = XMLInputFactory.newInstance().createXMLStreamReader(is);
      reader.nextTag();
      parseNextObj();
    }

    public boolean hasNext() {
      return this.nextObj != null;
    }

    public CompoundRecord next() {
      if(this.nextObj == null) {
        throw new NoSuchElementException();
      } else {
        CompoundRecord thisObj = this.nextObj;
        this.parseNextObj();
        return thisObj;
      }
    }

    public void remove() {
      throw new RuntimeException("Cannot remove from from ToxmlFileSource");
    }

    private void parseNextObj() {
      try {
        int t = this.reader.nextTag();
        if (t == 1) {
          this.nextObj = ToxmlParser.parseInternal(this.reader, CompoundRecord.class, new ToxmlErrorHandler[0]);
        }
        else {
          this.nextObj = null;
        }

      }
      catch (RuntimeException re) {
        throw re;
      }
      catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }

  private static class ToxmlIterators implements CloseableStream<CompoundRecord>, Iterator<CompoundRecord> {
    private List<File> files;
    private int fileIdx = -1;
    private ToxmlFileSource<CompoundRecord>.ToxmlIterator currentIterator;

    public ToxmlIterators(List<File> files) {
      this.files = files;
      nextFile();
    }

    @Override
    public boolean hasNext() {
      if (currentIterator == null) {
        return false;
      }
      else {
        if (currentIterator.hasNext()) {
          return true;
        }
        else {
          nextFile();
          return hasNext();
        }
      }
    }

    @Override
    public CompoundRecord next() {
      if (currentIterator == null) {
        throw new NoSuchElementException("The last toxml source has been exhausted or closed");
      }
      return currentIterator.next();
    }

    private void nextFile() {
      try {
        if (currentIterator != null) {
          try { currentIterator.close(); } catch (Throwable t) { }
          currentIterator = null;
        }
        fileIdx++;
        if (fileIdx < files.size()) {
          currentIterator = new ToxmlFileSource<>(files.get(fileIdx), CompoundRecord.class).iterator();
        }
      }
      catch (RuntimeException re) {
        throw re;
      }
      catch (Exception e) {
        throw new RuntimeException(e);
      }
    }

    @Override
    public Stream<CompoundRecord> getStream() {
      return StreamSupport.stream(Spliterators.spliteratorUnknownSize(
              this, Spliterator.ORDERED | Spliterator.NONNULL), false);
    }

    @Override
    public void close() throws IOException {
      if (currentIterator != null) {
        try { currentIterator.close(); } catch (Throwable t) { }
        currentIterator = null;
      }
    }
  }
}
