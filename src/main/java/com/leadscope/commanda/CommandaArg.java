/**
 * Copyright 2016 Leadscope, Inc. All rights reserved.
 * LEADSCOPE PROPRIETARY and CONFIDENTIAL. Use is subject to license terms.
 */
package com.leadscope.commanda;

public interface CommandaArg {
  /**
   * Gets the name used on the command line, not including the hyphen. E.g. returning
   * "csv" would be used on the command line as -csv
   * @return the arg name
   */
  String getArgName();

  /**
   * Gets a brief description displayed in the usage message
   * @return a brief description
   */
  String getDescription();
}
