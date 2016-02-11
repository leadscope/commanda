/**
 * Copyright 2016 Leadscope, Inc. All rights reserved.
 * LEADSCOPE PROPRIETARY and CONFIDENTIAL. Use is subject to license terms.
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
