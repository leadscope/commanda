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
