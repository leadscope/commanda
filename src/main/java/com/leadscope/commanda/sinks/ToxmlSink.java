/**
 * Copyright 2016 Leadscope, Inc. All rights reserved.
 * LEADSCOPE PROPRIETARY and CONFIDENTIAL. Use is subject to license terms.
 */
package com.leadscope.commanda.sinks;

import com.leadscope.stucco.io.ToxmlWriter;
import com.leadscope.stucco.model.CompoundRecord;
import pl.joegreen.lambdaFromString.TypeReference;

import java.io.OutputStream;
import java.util.stream.Stream;

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
