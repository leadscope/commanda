/**
 * Copyright 2016 Leadscope, Inc. All rights reserved.
 * LEADSCOPE PROPRIETARY and CONFIDENTIAL. Use is subject to license terms.
 */
package com.leadscope.commanda.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility for combining and iterating over files
 */
public class FileEnumeration implements Enumeration<InputStream> {
  private List<File> files;
  private int idx = 0;

  public FileEnumeration(List<File> files) {
    this.files = files;
  }

  @Override
  public boolean hasMoreElements() {
    return idx < files.size();
  }

  @Override
  public InputStream nextElement() {
    try {
      return new FileInputStream(files.get(idx++));
    }
    catch (RuntimeException re) {
      throw re;
    }
    catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static InputStream concatFiles(List<File> files) {
    for (File file : files) {
      if (!file.exists()) {
        throw new RuntimeException("File does not exist: " + file.getAbsolutePath());
      }
      if (!file.canRead()) {
        throw new RuntimeException("File cannot be read: " + file.getAbsolutePath());
      }
    }
    return new SequenceInputStream(new FileEnumeration(files));
  }

  public static InputStream concatFilenames(List<String> fileNames) {
    return concatFiles(fileNames.stream().map(File::new).collect(Collectors.toList()));
  }
}
