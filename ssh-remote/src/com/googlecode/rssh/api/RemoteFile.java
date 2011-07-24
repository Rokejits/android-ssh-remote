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
public class RemoteFile implements Parcelable {

  private String filePath;

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

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(filePath);
    dest.writeInt(isDirectory ? 1 : 0);
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
}
