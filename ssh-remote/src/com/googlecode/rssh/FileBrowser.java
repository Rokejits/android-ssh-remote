
package com.googlecode.rssh;

import com.googlecode.rssh.api.RemoteFile;
import com.googlecode.rssh.shell.CommunicationService;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Anton Novikov
 */
public class FileBrowser extends ListActivity {

  public static final String EXTRA_PICK_RESULT = "pick_result";

  private static final String HOME_DIR = ".";

  private ArrayAdapter<RemoteFile> adapter;

  private CommandServiceConnection connection;

  private Messenger responseMessenger;

  private String currentDir;

  private List<RemoteFile> dirContent;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    StateHolder holder = (StateHolder) getLastNonConfigurationInstance();
    if (holder != null) {
      connection = holder.connection;
      responseMessenger = holder.responseMessenger;
      currentDir = holder.currentDir;
    } else {
      connection = new CommandServiceConnection();
      responseMessenger = new Messenger(new CommandResponseHandler());
      currentDir = HOME_DIR;
    }

    this.dirContent = new ArrayList<RemoteFile>();
    adapter = new FileBrowserAdapter(getApplicationContext(), dirContent);
    setListAdapter(adapter);

  }

  @Override
  protected void onStart() {
    super.onStart();
    bindService(new Intent(getApplicationContext(), CommunicationService.class), connection,
        BIND_AUTO_CREATE);
  }

  @Override
  public StateHolder onRetainNonConfigurationInstance() {
    StateHolder holder = new StateHolder();
    holder.connection = this.connection;
    holder.responseMessenger = this.responseMessenger;
    holder.currentDir = this.currentDir;
    return holder;
  }

  @Override
  protected void onResume() {
    super.onResume();
    listDir();
  }

  @Override
  public void onBackPressed() {
    currentDir = new File(currentDir).getParent();
    if (TextUtils.isEmpty(currentDir)) {
      super.onBackPressed();
    } else {
      listDir();
    }
  }

  @Override
  protected void onStop() {
    super.onStop();
    if (connection.isServiceBound()) {
      unbindService(connection);
    }
  }

  @Override
  protected void onDestroy() {
    setListAdapter(null);
    super.onDestroy();
  }

  @Override
  protected void onListItemClick(ListView l, View v, int position, long id) {
    RemoteFile file = (RemoteFile) l.getItemAtPosition(position);
    if (file.isDirectory()) {
      currentDir = file.getFilePath();
      listDir();
    } else {
      // TODO: implement file picker;

      finish();
    }
  }

  private void listDir() {
    setTitle(currentDir);
    Message command = Message.obtain(null, CommunicationService.COMMAND_LS);
    Bundle data = new Bundle();
    data.putString(CommunicationService.INPUT_LIST_DIR, currentDir);
    command.setData(data);
    command.replyTo = responseMessenger;
    connection.sendCommand(command);
  }

  private class CommandResponseHandler extends Handler {
    @Override
    public void handleMessage(Message msg) {
      switch (msg.what) {
        case CommunicationService.COMMAND_LS:
          Bundle data = msg.getData();
          ArrayList<RemoteFile> content = data
              .getParcelableArrayList(CommunicationService.OUTPUT_LIST_DIR);
          dirContent.clear();
          dirContent.addAll(content);
          Collections.sort(dirContent);
          adapter.notifyDataSetChanged();
          break;
        default:
          super.handleMessage(msg);
          break;
      }
    }
  }

  private static class FileBrowserAdapter extends ArrayAdapter<RemoteFile> {

    public FileBrowserAdapter(Context context, List<RemoteFile> dirContent) {
      super(context, R.layout.browser_item, dirContent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      FileItemHolder holder;
      View row;
      if (convertView == null) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(
            LAYOUT_INFLATER_SERVICE);
        row = inflater.inflate(R.layout.browser_item, parent, false);
        holder = new FileItemHolder();
        holder.fileName = (TextView) row.findViewById(R.id.file_name);
        holder.icon = (ImageView) row.findViewById(R.id.file_icon);
        row.setTag(holder);
      } else {
        row = convertView;
        holder = (FileItemHolder) row.getTag();
      }

      RemoteFile file = getItem(position);
      String path = file.getFilePath();
      int separatorIndex = path.lastIndexOf(File.separator);
      String fileName = (separatorIndex < 0) ? path : path.substring(separatorIndex + 1,
          path.length());
      holder.fileName.setText(fileName);
      holder.icon.setImageResource(file.isDirectory() ? R.drawable.ic_folder : R.drawable.ic_file);
      return row;
    }
  }

  private static class FileItemHolder {
    private ImageView icon;

    private TextView fileName;
  }

  private static class StateHolder {
    private CommandServiceConnection connection;

    private Messenger responseMessenger;

    private String currentDir;
  }
}
