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
