/*
 * Copyright (c) 1993-2011. EPAM Systems.
 * All Rights Reserved.
 */

package com.googlecode.rssh.api;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Anton Novikov
 */
public class RemoteFile implements Parcelable, Comparable<RemoteFile> {

  private String filePath;

  private String parentPath;

  private boolean isDirectory;

  /**
   * Creates an instance of RemoteFile.
   */
  public RemoteFile() {}

  /**
   * Creates an instance of RemoteFile.
   * 
   * @param parcel
   */
  private RemoteFile(Parcel parcel) {
    this.filePath = parcel.readString();
    this.isDirectory = parcel.readInt() == 1;
    this.parentPath = parcel.readString();
  }

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

  public String getParentPath() {
    return parentPath;
  }

  public void setParentPath(String parentPath) {
    this.parentPath = parentPath;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(filePath);
    dest.writeInt(isDirectory ? 1 : 0);
    dest.writeString(parentPath);
  }

  public static final Parcelable.Creator<RemoteFile> CREATOR = new Parcelable.Creator<RemoteFile>() {

    @Override
    public RemoteFile createFromParcel(Parcel source) {
      return new RemoteFile(source);
    }

    @Override
    public RemoteFile[] newArray(int size) {
      return new RemoteFile[size];
    }
  };

  @Override
  public int compareTo(RemoteFile another) {
    if (this == another) {
      return 0;
    }
    if (another == null || (isDirectory() && !another.isDirectory())) {
      // Show directories above files
      return -1;
    }
    if (!isDirectory() && another.isDirectory()) {
      // Show files below directories
      return 1;
    }
    // Sort the directories alphabetically
    return getFilePath().compareToIgnoreCase(another.getFilePath());
  }
}
