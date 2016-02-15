/**
 * Copyright 2016 Leadscope, Inc. All rights reserved.
 * LEADSCOPE PROPRIETARY and CONFIDENTIAL. Use is subject to license terms.
 */
package com.leadscope.commanda;

import com.leadscope.commanda.lambda.DefaultLambdaImports;
import com.leadscope.commanda.lambda.LambdaUtil;
import com.leadscope.commanda.maps.*;
import com.leadscope.commanda.sinks.CommandaSink;
import com.leadscope.commanda.sinks.CommandaSinks;
import com.leadscope.commanda.sources.CommandaSource;
import com.leadscope.commanda.sources.CommandaSources;
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
    this(DefaultLambdaImports.imports, DefaultLambdaImports.staticImports, args);
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
    this.imports = imports;
    this.staticImports = staticImports;

    LinkedList<String> argsList = new LinkedList<>(Arrays.asList(args));

    for (Iterator<String> argIter = argsList.iterator(); argIter.hasNext(); ) {
      String nextArg = argIter.next();
      Optional<CommandaSource<?>> argSource = CommandaSources.forArg(nextArg);
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
      source = CommandaSources.defaultSource;
    }

    for (Iterator<String> argIter = argsList.iterator(); argIter.hasNext(); ) {
      String nextArg = argIter.next();
      Optional<CommandaSink<?>> argSink = CommandaSinks.forArg(nextArg);
      if (argSink.isPresent()) {
        sink = argSink.get();
        argIter.remove();
        break;
      }
    }

    TypeReference inputType = source.getElementType();
    while (!argsList.isEmpty()) {
      CommandaMap nextMap = popNextMap(inputType, argsList);
      streamMaps.add(nextMap);
      inputType = nextMap.getOutputType();
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

  private static String argUsage(CommandaSource<?> a) {
    return "    -" +
            a.getArgName() +
            pad(CommandaSources.maxArgLength() - a.getArgName().length() + 2) +
            a.getDescription();
  }

  private static String argUsage(CommandaMap<?, ?> a) {
    return "    -" +
            a.getArgName() +
            pad(CommandaMaps.maxArgLength() - a.getArgName().length() + 2) +
            a.getDescription();
  }

  private static String argUsage(CommandaSink<?> a) {
    return "    -" +
            a.getArgName() +
            pad(CommandaSinks.maxArgLength() - a.getArgName().length() + 2) +
            a.getDescription();
  }

  private static void usageExit() {
    System.err.println();
    System.err.println("Usage: cmda [source-operand] [file...] [map-operands...] [sink-operand]");
    System.err.println();
    System.err.println("  source-operands:");
    CommandaSources.sources.stream()
            .map(Commanda::argUsage)
            .forEachOrdered(System.err::println);
    System.err.println();
    System.err.println("  map-operands:");
    CommandaMaps.maps.stream()
            .map(Commanda::argUsage)
            .forEachOrdered(System.err::println);
    System.err.println();
    System.err.println("  sink-operands:");
    CommandaSinks.sinks.stream()
            .map(Commanda::argUsage)
            .forEachOrdered(System.err::println);
    System.exit(1);
  }

  private TypeReference nextInputType(LinkedList<String> argList) {
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
      if ("-e".equals(mapArg) || "-ne".equals(mapArg)) {
        throw new RuntimeException("Cannot have more than one adjacent lambda expression (-e or -ne)");
      }
      else {
        Optional<CommandaMap<?, ?>> map = CommandaMaps.forArg(mapArg);
        if (map.isPresent()) {
          return map.get().getInputType();
        }
        else {
          throw new RuntimeException("Unknown map argument: " + mapArg);
        }
      }
    }
  }

  private CommandaMap popNextMap(TypeReference inputType, LinkedList<String> argList) {
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
        return new LambdaStreamMap(imports, staticImports, codeArg, inputType, nextInputType(argList));
      }
      else if ("-ne".equals(mapArg)) {
        return new LambdaElementMap(imports, staticImports, codeArg, inputType, nextInputType(argList));
      }
      else {
        return new LambdaModifier(imports, staticImports, codeArg, inputType);
      }
    }
    else {
      Optional<CommandaMap<?, ?>> map = CommandaMaps.forArg(mapArg);
      if (map.isPresent()) {
        return map.get();
      }
      else {
        throw new RuntimeException("Unknown map argument: " + mapArg);
      }
    }
  }

  /**
   * @param args the arguments as passed in from the command-line
   */
  public static void main(String[] args) {
    if (Arrays.asList(args).contains("--help")) {
      usageExit();
    }
    try {
      Commanda commanda = new Commanda(args);
      commanda.run(System.in, System.out::println, System.out);
    }
    catch (Throwable t) {
      t.printStackTrace();
      usageExit();
    }
  }
}
