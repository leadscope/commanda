/**
 * Copyright 2016 Leadscope, Inc. All rights reserved.
 * LEADSCOPE PROPRIETARY and CONFIDENTIAL. Use is subject to license terms.
 */
package com.leadscope.commanda.maps;

import com.leadscope.commanda.lambda.DefaultLambdaImports;
import com.leadscope.commanda.lambda.DynamicLambdaFactory;
import pl.joegreen.lambdaFromString.LambdaFactoryConfiguration;
import pl.joegreen.lambdaFromString.TypeReference;

import java.util.List;
import java.util.function.Function;

/**
 * A compiled lambda expression that maps each element from the input stream to an element
 * in the output stream
 */
@SuppressWarnings("unchecked")
public class LambdaElementMap implements CommandaElementMap {
  private Function lambda;
  private TypeReference inputType;
  private TypeReference outputType;

  /**
   * A new lambda element map that uses the DefaultLambdaImports
   * @param lambdaString the lambda expression code
   * @param inputType the type of elements contained in the input stream
   * @param outputType the type of elements contained in the output stream
   */
  public LambdaElementMap(String lambdaString, TypeReference inputType, TypeReference outputType) {
    this(DefaultLambdaImports.imports,DefaultLambdaImports.staticImports,
            lambdaString, inputType, outputType);
  }

  /**
   * @param imports the default list of class imports to use while compiling the lambda
   * @parma staticImports the default list of static imports to use while compiling the lambda
   * @param lambdaString the lambda expression code
   * @param inputType the type of elements contained in the input stream
   * @param outputType the type of elements contained in the output stream
   */
  public LambdaElementMap(
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
    return "ne";
  }

  @Override
  public String getDescription() {
    return "Maps elements with the provided lambda";
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
    return Function.class.getCanonicalName() + "<" +
            inputType.toString() + ", " +
            outputType.toString() + ">";
  }
}
