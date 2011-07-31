
package com.googlecode.rssh;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

/**
 * @author Anton Novikov
 */
public class CommandServiceConnection implements ServiceConnection {
  private static final Logger LOG = LoggerFactory.getLogger(CommandServiceConnection.class);

  private Messenger commandMessenger;

  @Override
  public void onServiceConnected(ComponentName name, IBinder service) {
    commandMessenger = new Messenger(service);
  }

  @Override
  public void onServiceDisconnected(ComponentName name) {
    commandMessenger = null;
  }

  public boolean isServiceBound() {
    return commandMessenger != null;
  }

  public void sendCommand(Message command) {
    try {
      if (isServiceBound()) {
        commandMessenger.send(command);
      }
    } catch (RemoteException e) {
      LOG.error("Command service no longer available.", e);
    }
  }
}
