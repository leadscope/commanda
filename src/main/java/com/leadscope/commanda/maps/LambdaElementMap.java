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

import pl.joegreen.lambdaFromString.DynamicTypeReference;
import pl.joegreen.lambdaFromString.LambdaFactory;
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

      LambdaFactory factory = LambdaFactory.get(config);

      lambda = (Function)factory.createLambda(lambdaString, getTypeReference());
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

  private DynamicTypeReference getTypeReference() {
    return new DynamicTypeReference(Function.class.getCanonicalName() + "<" +
            inputType.toString() + ", " +
            outputType.toString() + ">");
  }
}
