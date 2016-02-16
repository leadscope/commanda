/**
 * Copyright 2016 Leadscope, Inc. All rights reserved.
 * LEADSCOPE PROPRIETARY and CONFIDENTIAL. Use is subject to license terms.
 */
package com.leadscope.commanda;

import com.leadscope.commanda.maps.CommandaMap;
import com.leadscope.commanda.sinks.CommandaSink;
import com.leadscope.commanda.sources.CommandaSource;

import java.util.*;
import java.util.stream.Stream;

/**
 * Defines the arguments that will be available to an execution of the commanda
 * application
 */
public class CommandaArgs {
  private CommandaSource<?> defaultSource;
  private ArgList<CommandaSource<?>> sources;
  private ArgList<CommandaMap<?, ?>> maps;
  private ArgList<CommandaSink<?>> sinks;

  public CommandaArgs(
          CommandaSource<?> defaultSource,
          List<CommandaSource<?>> sources,
          List<CommandaMap<?, ?>> maps,
          List<CommandaSink<?>> sinks) {
    this.defaultSource = defaultSource;
    this.sources = new ArgList<>(sources);
    this.maps = new ArgList<>(maps);
    this.sinks = new ArgList<>(sinks);
  }

  public CommandaSource<?> getDefaultSource() {
    return defaultSource;
  }

  public ArgList<CommandaSource<?>> getSources() {
    return sources;
  }

  public ArgList<CommandaMap<?, ?>> getMaps() {
    return maps;
  }

  public ArgList<CommandaSink<?>> getSinks() {
    return sinks;
  }

  /**
   * Looks for duplicates among the arguments and throws a RuntimeException if a duplicate
   * is found
   */
  public void checkForDuplicates() {
    Set<String> argNames = new HashSet<>();
    Arrays.asList(sources, maps, sinks).stream()
            .flatMap(list -> list.args.stream())
            .forEach(arg -> {
              if (argNames.contains(arg.getArgName())) {
                throw new RuntimeException("Duplicate definition for arg: " + arg.getArgName());
              }
              else {
                argNames.add(arg.getArgName());
              }
            });
  }

  /**
   * A utility wrapper for a list of arguments
   * @param <T> the type of argument
   */
  public static class ArgList<T extends CommandaArg> {
    private List<T> args;
    public ArgList(List<T> args) {
      this.args = args;
    }

    public Stream<T> stream() {
      return args.stream();
    }

    /**
     * Gets the longest argument name in the collection (not including the -)
     * @return the max argument name
     */
    public int maxArgLength() {
      return args.stream()
              .mapToInt(s -> s.getArgName().length())
              .max().getAsInt();
    }

    /**
     * Gets the argument matching the command line argument (including the -)
     * @param arg the argument from the command line (must include the -)
     * @return a matching argument object if present
     */
    public Optional<T> forArg(String arg) {
      return args.stream()
              .filter(a -> arg.equals("-" + a.getArgName()))
              .findAny();
    }
  }
}
