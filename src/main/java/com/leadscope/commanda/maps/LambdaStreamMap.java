/**
 * Copyright 2016 Leadscope, Inc. All rights reserved.
 * LEADSCOPE PROPRIETARY and CONFIDENTIAL. Use is subject to license terms.
 */
package com.leadscope.commanda.maps;

import com.leadscope.commanda.lambda.DynamicLambdaFactory;
import pl.joegreen.lambdaFromString.LambdaFactoryConfiguration;
import pl.joegreen.lambdaFromString.TypeReference;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * A compiled lambda expression that maps an entire stream to another stream
 */
@SuppressWarnings("unchecked")
public class LambdaStreamMap implements CommandaStreamMap {
  private Function lambda;
  private TypeReference inputType;
  private TypeReference outputType;

  /**
   * @param imports the default list of class imports to use while compiling the lambda
   * @parma staticImports the default list of static imports to use while compiling the lambda
   * @param lambdaString the lambda expression code
   * @param inputType the type of elements contained in the input stream
   * @param outputType the type of elements contained in the output stream
   */
  public LambdaStreamMap(
          List<? extends Class> imports,
          List<String> staticImports,
          String lambdaString,
          TypeReference inputType, TypeReference outputType) {
    this.inputType = inputType;
    this.outputType = outputType;

    try {
      LambdaFactoryConfiguration config = LambdaFactoryConfiguration.get()
              .withImports(imports.toArray(new Class[0]))
              .withStaticImports(staticImports.toArray(new String[0]));

      DynamicLambdaFactory factory = DynamicLambdaFactory.get(config);

      lambda = factory.createLambda(lambdaString, getTypeString());
    }
    catch (RuntimeException re) {
      throw re;
    }
    catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public String getArgName() {
    return "e";
  }

  @Override
  public String getDescription() {
    return "Maps the entire stream to another stream with the provided lambda";
  }

  @Override
  public TypeReference getInputType() {
    return inputType;
  }

  @Override
  public TypeReference getOutputType() {
    return outputType;
  }

  @Override
  public Function getFunction() {
    return lambda;
  }

  private String getTypeString() {
    return Function.class.getCanonicalName() +"<" +
            Stream.class.getCanonicalName() + "<" + inputType.toString() + ">, " +
            Stream.class.getCanonicalName() + "<" + outputType.toString() + ">>";
  }
}
