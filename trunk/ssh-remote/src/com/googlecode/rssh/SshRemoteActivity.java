
package com.googlecode.rssh;

import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.ViewById;
import com.googlecode.androidannotations.annotations.res.DrawableRes;
import com.googlecode.rssh.api.RemoteFile;
import com.googlecode.rssh.core.SshRemoteApp;
import com.googlecode.rssh.settings.ConfigActivity;
import com.googlecode.rssh.shell.CommunicationService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

@EActivity(R.layout.main)
public class SshRemoteActivity extends Activity {

  public static final String ACTION_SSH_REMOTE = "com.googlecode.rssh.actions.SSH_REMOTE";

  private static final Logger LOG = LoggerFactory.getLogger(SshRemoteActivity.class);

  @ViewById(R.id.stop)
  ImageButton stop;

  @ViewById(R.id.pause)
  ImageButton pause;

  @ViewById(R.id.forward)
  ImageButton forward;

  @ViewById(R.id.rewind)
  ImageButton rewind;

  // ImageButton volumeUp;
  // ImageButton volumeDown;

  @ViewById(R.id.connect)
  ImageButton connect;

  @DrawableRes(R.drawable.ic_connected)
  Drawable icConnected;

  @DrawableRes(R.drawable.ic_disconnected)
  Drawable icDisconnected;

  private CommandServiceConnection connection;

  private Messenger responseMessenger;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    StateHolder stateHolder = (StateHolder) getLastNonConfigurationInstance();
    if (stateHolder == null) {
      responseMessenger = new Messenger(new CommandResponseHandler());
      connection = new CommandServiceConnection();
    } else {
      responseMessenger = stateHolder.responseMessenger;
      connection = stateHolder.connection;
    }

    bindService(new Intent(getApplicationContext(), CommunicationService.class), connection,
        BIND_AUTO_CREATE);
    boolean isSshConnected = ((SshRemoteApp) getApplication()).isSshConnected();
    connect.setImageDrawable(isSshConnected ? icConnected : icDisconnected);
  }

  @Override
  protected void onDestroy() {
    if (connection.isServiceBound()) {
      unbindService(connection);
    }

    super.onDestroy();
  }

  @Override
  public StateHolder onRetainNonConfigurationInstance() {
    StateHolder stateHolder = new StateHolder();
    stateHolder.responseMessenger = responseMessenger;
    stateHolder.connection = connection;
    return stateHolder;
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.main_menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.settings_page:
        Intent settingsIntent = new Intent(getApplicationContext(), ConfigActivity.class);
        startActivity(settingsIntent);
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  @Click(R.id.connect)
  void onConnectClick(View v) {
    Message command = Message.obtain(null, CommunicationService.COMMAND_CON_DISC);
    command.replyTo = responseMessenger;
    connection.sendCommand(command);
  }

  @Click(R.id.open)
  void onOpenClick(View v) {
    startActivityForResult(new Intent(getApplicationContext(), FileBrowser.class),
        R.id.file_browser_rq_id);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (RESULT_OK != resultCode) {
      LOG.warn("Requested activity has bad result.");
      return;
    }

    switch (requestCode) {
      case R.id.file_browser_rq_id:
        RemoteFile file = data.getParcelableExtra(FileBrowser.EXTRA_PICK_RESULT);
        // TODO: handle play of picked file.
        break;
      default:
        super.onActivityResult(requestCode, resultCode, data);
        break;
    }
  }

  private class CommandResponseHandler extends Handler {
    @Override
    public void handleMessage(Message msg) {
      switch (msg.what) {
        case CommunicationService.COMMAND_CON_DISC:
          if (msg.arg1 == 1) {
            connect.setImageDrawable(msg.arg2 == 1 ? icConnected : icDisconnected);
          }
          break;
        case CommunicationService.COMMAND_PAUSE:

          break;
        default:
          super.handleMessage(msg);
          break;
      }
    }
  }

  private static class StateHolder {
    private CommandServiceConnection connection;

    private Messenger responseMessenger;
  }
}
