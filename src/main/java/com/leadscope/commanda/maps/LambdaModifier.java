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

import com.leadscope.commanda.lambda.DynamicLambdaFactory;
import pl.joegreen.lambdaFromString.LambdaFactoryConfiguration;
import pl.joegreen.lambdaFromString.TypeReference;

import java.util.List;
import java.util.function.Consumer;

/**
 * A lambda expression that modifies stream elements in memory
 */
@SuppressWarnings("unchecked")
public class LambdaModifier extends CommandaModifier {
  private Consumer lambda;
  private TypeReference type;

  /**
   * @param imports the default list of class imports to use while compiling the lambda
   * @parma staticImports the default list of static imports to use while compiling the lambda
   * @param lambdaString the lambda expression code
   * @param type the type of elements that will be modified
   */
  public LambdaModifier(
          List<? extends Class> imports,
          List<String> staticImports,
          String lambdaString,
          TypeReference type) {
    this.type = type;

    try {
      LambdaFactoryConfiguration config = LambdaFactoryConfiguration.get()
              .withImports(imports.toArray(new Class[0]))
              .withStaticImports(staticImports.toArray(new String[0]));

      DynamicLambdaFactory factory = DynamicLambdaFactory.get(config);

      lambda = (Consumer)factory.createLambda(lambdaString, getTypeString());
    }
    catch (RuntimeException re) {
      throw re;
    }
    catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Consumer getConsumer() {
    return lambda;
  }

  @Override
  public TypeReference getInputType() {
    return type;
  }

  @Override
  public TypeReference getOutputType() {
    return type;
  }

  @Override
  public String getArgName() {
    return "me";
  }

  @Override
  public String getDescription() {
    return "Modifies each element in memory with the provided consumer lambda (no return value)";
  }

  private String getTypeString() {
    return Consumer.class.getCanonicalName() + "<" + type.toString() + ">";
  }
}
