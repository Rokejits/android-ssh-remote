
package com.googlecode.rssh.settings;

import java.io.Serializable;

/**
 * @author Anton Novikov
 */
public class Profile implements Serializable {

  private static final long serialVersionUID = 4004873075180105564L;

  private String startApp;

  private String pause;

  private String stop;

  private String rewind;

  private String forward;

  private String volumeUp;

  private String volumeDown;

  public String getStartApp() {
    return startApp;
  }

  public void setStartApp(String startApp) {
    this.startApp = startApp;
  }

  public String getPause() {
    return pause;
  }

  public void setPause(String pause) {
    this.pause = pause;
  }

  public String getStop() {
    return stop;
  }

  public void setStop(String stop) {
    this.stop = stop;
  }

  public String getRewind() {
    return rewind;
  }

  public void setRewind(String rewind) {
    this.rewind = rewind;
  }

  public String getForward() {
    return forward;
  }

  public void setForward(String forward) {
    this.forward = forward;
  }

  public String getVolumeUp() {
    return volumeUp;
  }

  public void setVolumeUp(String volumeUp) {
    this.volumeUp = volumeUp;
  }

  public String getVolumeDown() {
    return volumeDown;
  }

  public void setVolumeDown(String volumeDown) {
    this.volumeDown = volumeDown;
  }

  @Override
  public String toString() {
    return getStartApp();
  }
}
