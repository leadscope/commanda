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
package com.leadscope.commanda.sinks;

import com.leadscope.stucco.io.ToxmlWriter;
import com.leadscope.stucco.model.CompoundRecord;
import pl.joegreen.lambdaFromString.TypeReference;

import java.io.OutputStream;
import java.util.stream.Stream;

/**
 * A commanda sink that writes a stream of CompoundRecord elements to a ToxML document
 */
public class ToxmlSink implements CommandaSink<CompoundRecord> {
  @Override
  public TypeReference<CompoundRecord> getInputType() {
    return new TypeReference<CompoundRecord>(){};
  }

  @Override
  public void consume(Stream<CompoundRecord> input, OutputStream output) {
    ToxmlWriter writer = null;
    try {
      writer = new ToxmlWriter("Compounds", output);
      ToxmlWriter finalWriter = writer;
      input.forEachOrdered(cr -> {
        try {
          finalWriter.write("Compound", cr);
        }
        catch (RuntimeException re) {
          throw re;
        }
        catch (Exception e) {
          throw new RuntimeException(e);
        }
      });
    }
    catch (RuntimeException re) {
      throw re;
    }
    catch (Exception e) {
      throw new RuntimeException(e);
    }
    finally {
      if (writer != null) {
        try { writer.close(); } catch (Throwable t) { }
      }
    }
  }

  @Override
  public String getArgName() {
    return "toxOut";
  }

  @Override
  public String getDescription() {
    return "Outputs the compound records as a ToxML document";
  }
}
