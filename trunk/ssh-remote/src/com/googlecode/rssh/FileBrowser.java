
package com.googlecode.rssh;

import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.rssh.api.RemoteFile;
import com.googlecode.rssh.shell.CommunicationService;

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.Comparator;

/**
 * @author Anton Novikov
 */
@EActivity
public class FileBrowser extends ListActivity {

  private static final String HOME_DIR = "~";

  private static final RemoteFile INITIAL_LOCATION;

  static {
    INITIAL_LOCATION = new RemoteFile();
    INITIAL_LOCATION.setDirectory(true);
    INITIAL_LOCATION.setFilePath(HOME_DIR);
  }

  private ArrayAdapter<RemoteFile> adapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    RemoteFile[] dirContent = RemoteFile.CREATOR.newArray(0);
    adapter = new FileBrowserAdapter(getApplicationContext(), dirContent);
  }

  private RemoteFile[] refreshFileList() {

    return null;
  }

  private class CommandResponseHandler extends Handler {
    @Override
    public void handleMessage(Message msg) {
      switch (msg.what) {
        case CommunicationService.COMMAND_LS:

          break;
        default:
          super.handleMessage(msg);
          break;
      }
    }
  }

  private static class FileBrowserAdapter extends ArrayAdapter<RemoteFile> {

    public FileBrowserAdapter(Context context, RemoteFile[] dirContent) {
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

  private static class FileComparator implements Comparator<RemoteFile> {
    @Override
    public int compare(RemoteFile f1, RemoteFile f2) {
      if (f1 == f2) {
        return 0;
      }
      if (f1.isDirectory() && !f2.isDirectory()) {
        // Show directories above files
        return -1;
      }
      if (!f1.isDirectory() && f2.isDirectory()) {
        // Show files below directories
        return 1;
      }
      // Sort the directories alphabetically
      return f1.getFilePath().compareToIgnoreCase(f2.getFilePath());
    }
  }
}
