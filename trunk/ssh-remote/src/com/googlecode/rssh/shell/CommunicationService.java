
package com.googlecode.rssh.shell;

import com.googlecode.rssh.R;
import com.googlecode.rssh.SshRemoteActivity_;
import com.googlecode.rssh.api.RemoteFile;
import com.googlecode.rssh.core.SshRemoteApp;
import com.googlecode.rssh.settings.ConfigActivity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Anton Novikov
 */
public class CommunicationService extends Service {

  private static final Logger LOG = LoggerFactory.getLogger(CommunicationService.class);

  public static final int COMMAND_START = 0;

  public static final int COMMAND_STOP = 1;

  public static final int COMMAND_PAUSE = 2;

  public static final int COMMAND_REWIND = 3;

  public static final int COMMAND_FASTFORWARD = 4;

  public static final int COMMAND_VOLUME_UP = 5;

  public static final int COMMAND_VOLUME_DOWN = 6;

  public static final int COMMAND_CON_DISC = 7;

  public static final int COMMAND_LS = 8;

  public static final String INPUT_LIST_DIR = "in_list_dir";

  public static final String OUTPUT_LIST_DIR = "out_list_dir";

  private final Messenger messenger = new Messenger(new CommandHandler());

  private SecureShell shell;

  private SshRemoteApp application;

  @Override
  public IBinder onBind(Intent intent) {
    return messenger.getBinder();
  }

  @Override
  public void onCreate() {
    super.onCreate();
    LOG.debug("Service is Created");
    application = (SshRemoteApp) getApplication();
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    return START_STICKY;
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    LOG.debug("Service is destroyed");
  }

  private void connect(Messenger replyTo) throws RemoteException {
    SharedPreferences prefs = PreferenceManager
        .getDefaultSharedPreferences(getApplicationContext());
    String host = prefs.getString(ConfigActivity.HOST_NAME, "");
    String port = prefs.getString(ConfigActivity.PORT, getString(R.string.defaultPort));
    String username = prefs.getString(ConfigActivity.USER_NAME, "");
    String password = prefs.getString(ConfigActivity.PASSWORD, "");

    if (TextUtils.isEmpty(host) || TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
      Toast.makeText(getApplicationContext(), R.string.connection_not_configured,
          Toast.LENGTH_SHORT).show();
    } else {
      shell = new SecureShell();
      boolean connected = shell.login(host, Integer.parseInt(port), username, password);
      // first arg is command status, second means that connect command
      // performed.
      Message response = Message.obtain(null, COMMAND_CON_DISC, connected ? 1 : 0, 1);
      replyTo.send(response);
      application.setSshConnected(connected);
      if (connected) {
        // start service as foreground and show service icon
        Notification notif = new Notification(R.drawable.stat_app_icon, "",
            System.currentTimeMillis());
        Intent intent = new Intent(getApplicationContext(), SshRemoteActivity_.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT);
        notif.flags |= Notification.FLAG_ONGOING_EVENT;
        notif.setLatestEventInfo(getApplicationContext(), getString(R.string.app_name), "",
            contentIntent);
        startForeground(R.id.ssh_connected_icon, notif);
      }
    }
  }

  private void disconnect(Messenger replyTo) throws RemoteException {
    boolean disconnected = shell.logout();
    // first arg is command status, second means that disconnection performed.
    Message response = Message.obtain(null, COMMAND_CON_DISC, disconnected ? 1 : 0, 0);
    replyTo.send(response);
    if (disconnected) {
      shell = null;
      application.setSshConnected(false);
      stopForeground(true);
      stopSelf();
    }
  }

  private void pause() {

  }

  private void listDir(String dir, Messenger replyTo) throws RemoteException {
    try {
      ArrayList<RemoteFile> content = shell.browseDir(dir);
      Message reply = Message.obtain(null, COMMAND_LS);
      Bundle data = new Bundle();
      data.putParcelableArrayList(OUTPUT_LIST_DIR, content);
      reply.setData(data);
      replyTo.send(reply);
    } catch (IOException e) {
      LOG.error("Unable to obtain remote dir content.", e);
    }
  }

  private void volumeUp() {

  }

  private void volumeDown() {

  }

  private void stop() {

  }

  private void rewind() {

  }

  private void fastforward() {

  }

  private class CommandHandler extends Handler {

    @Override
    public void handleMessage(Message msg) {
      try {
        switch (msg.what) {
          case COMMAND_CON_DISC:
            if (application.isSshConnected()) {
              disconnect(msg.replyTo);
            } else {
              connect(msg.replyTo);
            }
            break;
          case COMMAND_LS:
            Bundle msgData = msg.getData();
            String dir = msgData.getString(INPUT_LIST_DIR);
            listDir(dir, msg.replyTo);
            break;
          case COMMAND_START:

            break;
          case COMMAND_STOP:

            break;
          case COMMAND_PAUSE:

            break;
          case COMMAND_REWIND:

            break;
          case COMMAND_FASTFORWARD:

            break;
          case COMMAND_VOLUME_UP:

            break;
          case COMMAND_VOLUME_DOWN:

            break;
          default:
            super.handleMessage(msg);
            break;
        }
      } catch (RemoteException e) {
        LOG.error("Reply destination no longer exists.", e);
      }
    }
  }
}
