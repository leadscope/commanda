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
package com.leadscope.commanda;

import com.leadscope.commanda.lambda.DefaultLambdaImports;
import com.leadscope.commanda.lambda.LambdaUtil;
import com.leadscope.commanda.maps.*;
import com.leadscope.commanda.sinks.CommandaSink;
import com.leadscope.commanda.sources.CommandaSource;
import com.leadscope.commanda.util.CloseableStream;
import pl.joegreen.lambdaFromString.TypeReference;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * The main commanda application - see usage
 */
@SuppressWarnings("unchecked")
public class Commanda {
  private CommandaSource source;
  private CommandaSink sink;
  private List<File> files = new ArrayList<>();
  private List<CommandaMap> streamMaps = new ArrayList<>();
  private List<? extends Class> imports;
  private List<String> staticImports;

  /**
   * Sets up the commanda chain - see usage
   * @param args the arguments as passed in from the command-line
   */
  public Commanda(String... args) {
    this(DefaultLambdaImports.imports, DefaultLambdaImports.staticImports, DefaultCommandaArgs.defaultArgs(), args);
  }

  /**
   * Sets up the commanda chain - see usage
   * @param imports the default list of class imports to use while compiling the lambda
   * @parma staticImports the default list of static imports to use while compiling the lambda
   * @param args the arguments as passed in from the command-line
   */
  public Commanda(List<? extends Class> imports,
                  List<String> staticImports,
                  String... args) {
    this(imports, staticImports, DefaultCommandaArgs.defaultArgs(), args);
  }

  /**
   * Sets up the commanda chain - see usage
   * @param imports the default list of class imports to use while compiling the lambda
   * @parma staticImports the default list of static imports to use while compiling the lambda
   * @param availableArgs the available arguments for this
   * @param args the arguments as passed in from the command-line
   */
  public Commanda(List<? extends Class> imports,
                  List<String> staticImports,
                  CommandaArgs availableArgs,
                  String... args) {
    this.imports = imports;
    this.staticImports = staticImports;

    availableArgs.checkForDuplicates();

    LinkedList<String> argsList = new LinkedList<>(Arrays.asList(args));

    for (Iterator<String> argIter = argsList.iterator(); argIter.hasNext(); ) {
      String nextArg = argIter.next();
      Optional<CommandaSource<?>> argSource = availableArgs.getSources().forArg(nextArg);
      if (argSource.isPresent()) {
        source = argSource.get();
        argIter.remove();
        while (argIter.hasNext()) {
          nextArg = argIter.next();
          if (!nextArg.startsWith("-")) {
            files.add(new File(nextArg));
            argIter.remove();
          }
          else {
            break;
          }
        }
        break;
      }
    }

    if (source == null) {
      source = availableArgs.getDefaultSource();
    }
    else {
      argsList.stream()
              .filter(arg -> availableArgs.getSources().forArg(arg).isPresent())
              .forEach(arg -> {
                throw new RuntimeException("More than one source defined: -" + source.getArgName() +
                        " and " + arg);
              });
    }

    for (Iterator<String> argIter = argsList.iterator(); argIter.hasNext(); ) {
      String nextArg = argIter.next();
      Optional<CommandaSink<?>> argSink = availableArgs.getSinks().forArg(nextArg);
      if (argSink.isPresent()) {
        if (sink != null) {
          throw new RuntimeException("More than one sink argument provided: -" + sink.getArgName() +
                  " and " + argSink);
        }
        sink = argSink.get();
        argIter.remove();
      }
    }

    TypeReference inputType = source.getElementType();
    while (!argsList.isEmpty()) {
      CommandaMap nextMap = popNextMap(availableArgs, inputType, argsList);
      streamMaps.add(nextMap);
      inputType = nextMap.getOutputType();
    }

    if (sink != null && !sink.getInputType().toString().equals(inputType.toString())) {
      throw new RuntimeException("Input for sink: -" + sink.getArgName() +
              " does not match output type for last argument: " + inputType.toString());
    }
  }

  private TypeReference nextInputType(CommandaArgs availableArgs, LinkedList<String> argList) {
    if (argList.isEmpty()) {
      if (sink == null) {
        return LambdaUtil.STRING_TYPE;
      }
      else {
        return sink.getInputType();
      }
    }
    else {
      String mapArg = argList.getFirst();
      if ("-e".equals(mapArg) || "-ne".equals(mapArg) || "-me".equals(mapArg)) {
        throw new RuntimeException("Cannot have more than one adjacent lambda expression (-e, -ne, or -me)");
      }
      else {
        Optional<CommandaMap<?, ?>> map = availableArgs.getMaps().forArg(mapArg);
        if (map.isPresent()) {
          return map.get().getInputType();
        }
        else {
          throw new RuntimeException("Unknown map argument: " + mapArg);
        }
      }
    }
  }

  private CommandaMap popNextMap(CommandaArgs availableArgs, TypeReference inputType, LinkedList<String> argList) {
    String mapArg = argList.pop();
    if ("-e".equals(mapArg) || "-ne".equals(mapArg) || "-me".equals(mapArg)) {
      if (argList.isEmpty()) {
        throw new RuntimeException("Missing lambda code argument to: " + mapArg);
      }
      String codeArg = argList.pop();
      if (codeArg.startsWith("-")) {
        throw new RuntimeException("Missing lambda code argument to: " + mapArg);
      }
      if ("-e".equals(mapArg)) {
        return new LambdaStreamMap(imports, staticImports, codeArg, inputType, nextInputType(availableArgs, argList));
      }
      else if ("-ne".equals(mapArg)) {
        return new LambdaElementMap(imports, staticImports, codeArg, inputType, nextInputType(availableArgs, argList));
      }
      else {
        return new LambdaModifier(imports, staticImports, codeArg, inputType);
      }
    }
    else {
      Optional<CommandaMap<?, ?>> map = availableArgs.getMaps().forArg(mapArg);
      if (map.isPresent()) {
        if (!map.get().getInputType().toString().equals(inputType.toString())) {
          throw new RuntimeException("Input for argument: " + mapArg +
                  " does not match output type for previous argument: " + inputType.toString());
        }
        return map.get();
      }
      else {
        throw new RuntimeException("Unknown map argument: " + mapArg);
      }
    }
  }

  /**
   * Processes the stream using stdout if a sink is provided
   * @param stdin the standard input to use if no files are provided - this will NOT be closed
   * @param handler optional handler for processing the output - only used if no sink is provided
   */
  public void run(InputStream stdin, Consumer<String> handler) {
    run(stdin, handler, System.out);
  }

  /**
   * Processes the input stream passing mapped output strings to the provided handler if no sink
   * is provided
   * @param stdin the standard input to use if no files are provided - this will NOT be closed
   * @param handler optional handler for processing the output - only used if no sink is provided
   * @param stdout optional output stream for the sink to write to - only used if sink is provided
   */
  public void run(InputStream stdin, Consumer<String> handler, OutputStream stdout) {
    CloseableStream closeableStream = null;
    try {
      Stream stream;
      if (files.size() == 0) {
        if (stdin == null) {
          throw new RuntimeException("No stdin was provided and no files were included in the command line");
        }
        stream = source.stream(stdin);
      }
      else {
        closeableStream = source.stream(files);
        stream = closeableStream.getStream();
      }

      for (CommandaMap map : streamMaps) {
        if (map instanceof CommandaElementMap) {
          stream = stream.map(((CommandaElementMap) map).getFunction());
        }
        else if (map instanceof CommandaStreamMap) {
          stream = (Stream) ((CommandaStreamMap) map).getFunction().apply(stream);
        }
        else {
          throw new RuntimeException("Unsupported CommandaMap type: " + map.getClass());
        }
      }

      if (sink != null) {
        if (stdout == null) {
          throw new RuntimeException("No stdout was provided a sink was specified for output");
        }
        sink.consume(stream, stdout);
      }
      else {
        if (handler != null) {
          stream.forEachOrdered(handler);
        }
        else {
          stream.forEachOrdered(l -> {});
        }
      }
    }
    finally {
      if (closeableStream != null) {
        try { closeableStream.close(); } catch (Throwable t) { }
      }
    }
  }

  private static String pad(int length) {
    return new String(new char[length]).replace('\0', ' ');
  }

  private static String argUsage(int maxArgLength, CommandaArg a) {
    return "    -" +
            a.getArgName() +
            pad(maxArgLength - a.getArgName().length() + 2) +
            a.getDescription();
  }

  private static void usageExit(CommandaArgs args) {
    System.err.println();
    System.err.println("Usage: cmda [source-operand] [file...] [map-operands...] [sink-operand]");
    System.err.println();
    System.err.println("  source-operands:");
    args.getSources().stream()
            .map(a -> argUsage(args.getSources().maxArgLength(), a))
            .forEachOrdered(System.err::println);
    System.err.println();
    System.err.println("  map-operands:");
    args.getMaps().stream()
            .map(a -> argUsage(args.getMaps().maxArgLength(), a))
            .forEachOrdered(System.err::println);
    System.err.println();
    System.err.println("  sink-operands:");
    args.getSinks().stream()
            .map(a -> argUsage(args.getSinks().maxArgLength(), a))
            .forEachOrdered(System.err::println);
    System.exit(1);
  }

  /**
   * @param args the arguments as passed in from the command-line
   */
  public static void main(String[] args) {
    CommandaArgs availableArgs = DefaultCommandaArgs.defaultArgs();
    if (Arrays.asList(args).contains("--help")) {
      usageExit(availableArgs);
    }
    try {
      Commanda commanda = new Commanda(
              DefaultLambdaImports.imports,
              DefaultLambdaImports.staticImports,
              availableArgs,
              args);
      commanda.run(System.in, System.out::println, System.out);
    }
    catch (Throwable t) {
      t.printStackTrace();
      usageExit(availableArgs);
    }
  }
}
