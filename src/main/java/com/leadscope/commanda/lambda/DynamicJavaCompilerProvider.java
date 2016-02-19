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
package com.leadscope.commanda.lambda;

import org.eclipse.jdt.internal.compiler.tool.EclipseCompiler;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.util.Optional;

public class DynamicJavaCompilerProvider {
  static Optional<JavaCompiler> findDefaultJavaCompiler() {
    Optional<JavaCompiler> eclipseJavaCompiler = getEclipseJavaCompiler();
    Optional<JavaCompiler> jdkJavaCompiler = getJdkJavaCompiler();
    if(eclipseJavaCompiler.isPresent()){
      return eclipseJavaCompiler;
    }else if(jdkJavaCompiler.isPresent()){
      return jdkJavaCompiler;
    }else{
      return Optional.empty();
    }
  }

  static Optional<JavaCompiler> getEclipseJavaCompiler() {
    try {
      return Optional.of(new EclipseCompiler());
    } catch (NoClassDefFoundError err) {
      return Optional.empty();
    }
  }

  static Optional<JavaCompiler> getJdkJavaCompiler() {
    return Optional.ofNullable(ToolProvider.getSystemJavaCompiler());
  }
}
