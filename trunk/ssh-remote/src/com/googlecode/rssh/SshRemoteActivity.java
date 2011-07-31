
package com.googlecode.rssh;

import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.rssh.settings.ConfigActivity;
import com.googlecode.rssh.shell.CommunicationService;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.view.Menu;
import android.view.MenuItem;

@EActivity(R.layout.main)
public class SshRemoteActivity extends Activity {

  public static final String ACTION_SSH_REMOTE = "com.googlecode.rssh.actions.SSH_REMOTE";

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

    // TODO: move to connect command
    bindService(new Intent(getApplicationContext(), CommunicationService.class), connection,
        BIND_AUTO_CREATE);
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

  private class CommandResponseHandler extends Handler {
    @Override
    public void handleMessage(Message msg) {
      switch (msg.what) {
        case CommunicationService.COMMAND_CONNECT:

          break;
        case CommunicationService.COMMAND_DISCONNECT:

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
