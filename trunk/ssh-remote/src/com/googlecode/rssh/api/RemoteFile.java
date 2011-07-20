/*
 * Copyright (c) 1993-2011. EPAM Systems.
 * All Rights Reserved.
 */

package com.googlecode.rssh.api;

import java.io.Serializable;

/**
 * @author Anton Novikov
 */
public class RemoteFile implements Serializable {

  private static final long serialVersionUID = -9076455357965875256L;

  private String filePath;

  private boolean isDirectory;

  /**
   * Creates an instance of RemoteFile.
   */
  public RemoteFile() {}

  public String getFilePath() {
    return filePath;
  }

  public void setFilePath(String filePath) {
    this.filePath = filePath;
  }

  public boolean isDirectory() {
    return isDirectory;
  }

  public void setDirectory(boolean isDirectory) {
    this.isDirectory = isDirectory;
  }
}
