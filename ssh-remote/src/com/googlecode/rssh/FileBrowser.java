
package com.googlecode.rssh;

import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.rssh.api.RemoteFile;
import com.googlecode.rssh.shell.CommunicationService;

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
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
@EActivity
public class FileBrowser extends ListActivity {

  private static final String HOME_DIR = "~";

  private ArrayAdapter<RemoteFile> adapter;

  private CommandServiceConnection connection;

  private Messenger responseMessenger;

  private RemoteFile currentDir;

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
      currentDir = new RemoteFile();
      currentDir.setDirectory(true);
      currentDir.setFilePath(HOME_DIR);
    }

    this.dirContent = new ArrayList<RemoteFile>();
    adapter = new FileBrowserAdapter(getApplicationContext(), dirContent);
    setListAdapter(adapter);
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
    refreshFileList();
    super.onResume();
  }

  @Override
  public void onBackPressed() {
    if (currentDir.getParentPath() != null) {
      RemoteFile parent = new RemoteFile();
      parent.setDirectory(true);
      parent.setFilePath(currentDir.getParentPath());
      currentDir = parent;
      return;
    }
    super.onBackPressed();
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
      currentDir = file;
      refreshFileList();
    } else {
      // TODO: implement file picker;

      finish();
    }
  }

  private void refreshFileList() {
    Message command = Message.obtain(null, CommunicationService.COMMAND_LS);
    Bundle data = new Bundle();
    data.putParcelable(CommunicationService.INPUT_LIST_DIR, currentDir);
    command.setData(data);
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

    private RemoteFile currentDir;
  }
}
