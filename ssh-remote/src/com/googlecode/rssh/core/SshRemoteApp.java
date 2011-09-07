
package com.googlecode.rssh.core;

import android.app.Application;

/**
 * @author Anton Novikov
 */
public class SshRemoteApp extends Application {

  private boolean sshConnected;

  public boolean isSshConnected() {
    return sshConnected;
  }

  public void setSshConnected(boolean sshConnected) {
    this.sshConnected = sshConnected;
  }
}
