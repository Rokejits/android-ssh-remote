
package com.googlecode.rssh.shell;

import com.googlecode.rssh.R;
import com.googlecode.rssh.SshRemoteActivity_;
import com.googlecode.rssh.api.RemoteFile;
import com.googlecode.rssh.core.SshRemoteApp;
import com.googlecode.rssh.settings.ConfigActivity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
class ShellThread extends Thread {
  private static final Logger LOG = LoggerFactory.getLogger(ShellThread.class);

  private final SshRemoteApp application;

  private final Context appContext;

  private final SecureShell shell;

  private final NotificationManager notificationManager;

  private Handler commandHandler;

  public ShellThread(SshRemoteApp application) {
    this.application = application;
    this.appContext = application.getApplicationContext();
    notificationManager = (NotificationManager) appContext
        .getSystemService(Context.NOTIFICATION_SERVICE);
    this.shell = new SecureShell();
  }

  @Override
  public void run() {
    Looper.prepare();

    commandHandler = new CommandHandler();

    Looper.loop();
  }

  Handler getHandler() {
    return commandHandler;
  }

  private class CommandHandler extends Handler {

    @Override
    public void handleMessage(Message msg) {
      try {
        switch (msg.what) {
          case CommunicationService.COMMAND_CON_DISC:
            if (application.isSshConnected()) {
              disconnect(msg.replyTo);
            } else {
              connect(msg.replyTo);
            }
            break;
          case CommunicationService.COMMAND_LS:
            Bundle msgData = msg.getData();
            RemoteFile dir = msgData.getParcelable(CommunicationService.INPUT_LIST_DIR);
            listDir(dir, msg.replyTo);
            break;
          case CommunicationService.COMMAND_START:
            start();
            break;
          case CommunicationService.COMMAND_STOP:
            stop();
            break;
          case CommunicationService.COMMAND_PAUSE:
            pause();
            break;
          case CommunicationService.COMMAND_REWIND:
            rewind();
            break;
          case CommunicationService.COMMAND_FASTFORWARD:
            fastforward();
            break;
          case CommunicationService.COMMAND_VOLUME_UP:
            volumeUp();
            break;
          case CommunicationService.COMMAND_VOLUME_DOWN:
            volumeDown();
            break;
          default:
            super.handleMessage(msg);
            break;
        }
      } catch (RemoteException e) {
        LOG.error("Reply destination no longer exists.", e);
      }
    }

    private void connect(Messenger replyTo) throws RemoteException {
      SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(appContext);
      String host = prefs.getString(ConfigActivity.HOST_NAME, "");
      String port = prefs
          .getString(ConfigActivity.PORT, appContext.getString(R.string.defaultPort));
      String username = prefs.getString(ConfigActivity.USER_NAME, "");
      String password = prefs.getString(ConfigActivity.PASSWORD, "");

      if (TextUtils.isEmpty(host) || TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
        Toast.makeText(appContext, R.string.connection_not_configured, Toast.LENGTH_SHORT).show();
      } else {
        boolean connected = shell.login(host, Integer.parseInt(port), username, password);
        // first arg is command status, second means that connect command
        // performed.
        Message response = Message.obtain(null, CommunicationService.COMMAND_CON_DISC,
            connected ? 1 : 0, 1);
        replyTo.send(response);
        application.setSshConnected(connected);
        if (connected) {
          showIcon();
        }
      }
    }

    private void showIcon() {
      Notification notif = new Notification(R.drawable.stat_app_icon, "",
          System.currentTimeMillis());
      Intent intent = new Intent(appContext, SshRemoteActivity_.class);
      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
      PendingIntent contentIntent = PendingIntent.getActivity(appContext, 0, intent,
          PendingIntent.FLAG_UPDATE_CURRENT);
      notif.flags |= Notification.FLAG_ONGOING_EVENT;
      notif.setLatestEventInfo(appContext, appContext.getString(R.string.app_name), "",
          contentIntent);
      notificationManager.notify(R.id.ssh_connected_icon, notif);
    }

    private void disconnect(Messenger replyTo) throws RemoteException {
      boolean disconnected = shell.logout();
      // first arg is command status, second means that disconnection performed.
      Message response = Message.obtain(null, CommunicationService.COMMAND_CON_DISC,
          disconnected ? 1 : 0, 0);
      replyTo.send(response);
      if (disconnected) {
        application.setSshConnected(false);
        notificationManager.cancel(R.id.ssh_connected_icon);
      }
    }

    private void listDir(RemoteFile dir, Messenger replyTo) throws RemoteException {
      try {
        ArrayList<RemoteFile> content = shell.browseDir(dir.getFilePath());
        Message reply = Message.obtain(null, CommunicationService.COMMAND_LS);
        Bundle data = new Bundle();
        data.putParcelableArrayList(CommunicationService.OUTPUT_LIST_DIR, content);
        reply.setData(data);
        replyTo.send(reply);
      } catch (IOException e) {
        LOG.error("Unable to obtain remote dir content.", e);
      }
    }

    private void pause() {

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
  }
}
