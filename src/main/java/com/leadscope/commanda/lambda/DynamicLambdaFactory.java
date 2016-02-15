/**
 * Copyright 2016 Leadscope, Inc. All rights reserved.
 * LEADSCOPE PROPRIETARY and CONFIDENTIAL. Use is subject to license terms.
 */
package com.leadscope.commanda.lambda;

import pl.joegreen.lambdaFromString.*;
import pl.joegreen.lambdaFromString.classFactory.ClassCompilationException;
import pl.joegreen.lambdaFromString.classFactory.ClassFactory;

import javax.tools.JavaCompiler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

public class DynamicLambdaFactory {
  protected static Optional<JavaCompiler> DEFAULT_COMPILER = DynamicJavaCompilerProvider.findDefaultJavaCompiler();

  /**
   * Returns a LambdaFactory instance with default configuration.
   * @throws JavaCompilerNotFoundException if the library cannot find any java compiler
   */
  public static DynamicLambdaFactory get() {
    return get(LambdaFactoryConfiguration.get());
  }

  /**
   * Returns a LambdaFactory instance with the given configuration.
   * @throws JavaCompilerNotFoundException if the library cannot find any java compiler and it's not provided
   * in the configuration
   */
  public static DynamicLambdaFactory get(LambdaFactoryConfiguration configuration) {
    JavaCompiler compiler = getConfiguredOrDefaultCompiler(configuration);
    return new DynamicLambdaFactory(
            configuration.getDefaultHelperClassSourceProvider(),
            configuration.getClassFactory(),
            compiler,
            configuration.getImports(),
            configuration.getStaticImports());
  }

  private static JavaCompiler getConfiguredOrDefaultCompiler(LambdaFactoryConfiguration configuration) {
    return configuration.getJavaCompiler()
            .orElse(DEFAULT_COMPILER
                    .orElseThrow(JavaCompilerNotFoundException::new));
  }

  private final HelperClassSourceProvider helperProvider;
  private final ClassFactory classFactory;
  private final JavaCompiler javaCompiler;
  private final List<String> imports;
  private final List<String> staticImports;

  private DynamicLambdaFactory(HelperClassSourceProvider helperProvider, ClassFactory classFactory,
                        JavaCompiler javaCompiler, List<String> imports, List<String> staticImports) {
    this.helperProvider = helperProvider;
    this.classFactory = classFactory;
    this.javaCompiler = javaCompiler;
    this.imports = imports;
    this.staticImports = staticImports;
  }

  /**
   * Creates lambda from the given code.
   *
   * @param code          source of the lambda as you would write it in Java expression  {TYPE} lambda = ({CODE});
   * @param typeString    string definition of the lambda type
   * @throws LambdaCreationException when anything goes wrong (no other exceptions are thrown including runtimes),
   *                                 if the exception was caused by compilation failure it will contain a CompilationDetails instance describing them
   */
  public Object createLambda(String code, String typeString) throws LambdaCreationException {
    String helperClassSource = helperProvider.getHelperClassSource(typeString, code, imports, staticImports);
    try {
      Class<?> helperClass = classFactory.createClass(helperProvider.getHelperClassName(), helperClassSource, javaCompiler);
      Method lambdaReturningMethod = helperClass.getMethod(helperProvider.getLambdaReturningMethodName());
      return lambdaReturningMethod.invoke(null);
    } catch (ReflectiveOperationException | RuntimeException e) {
      throw new LambdaCreationException(e);
    } catch (ClassCompilationException classCompilationException) {
      // knows type of the cause so it get CompilationDetails
      throw new LambdaCreationException(classCompilationException);
    }
  }

  /**
   * Convenience wrapper for {@link #createLambda(String, String)}
   * which throws unchecked exception instead of checked one.
   *
   * @see #createLambda(String, String)
   */
  public Object createLambdaUnchecked(String code, String typeString) {
    try {
      return createLambda(code, typeString);
    } catch (LambdaCreationException e) {
      throw new LambdaCreationRuntimeException(e);
    }
  }
}
