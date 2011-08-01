/*
 * Copyright (c) 1993-2011. EPAM Systems.
 * All Rights Reserved.
 */

package com.googlecode.rssh.shell;

import com.googlecode.rssh.api.RemoteFile;
import com.googlecode.rssh.settings.Profile;

import net.schmizz.sshj.AndroidConfig;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.ConnectionException;
import net.schmizz.sshj.sftp.RemoteResourceFilter;
import net.schmizz.sshj.sftp.RemoteResourceInfo;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.text.TextUtils;

import java.io.IOException;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Anton Novikov
 */
public class SecureShell {
  private static final Logger LOG = LoggerFactory.getLogger(SecureShell.class);

  private Profile profile;

  private SSHClient client;

  private final RemoteResourceFilter mediaFileFilter = new FileFilter();

  SecureShell() {}

  public boolean login(String host, int port, String userName, String password) {
    try {
      client = new SSHClient(new AndroidConfig());
      client.addHostKeyVerifier(new PromiscuousVerifier());
      client.connect(host, port);
      client.authPassword(userName, password);
      return true;
    } catch (IOException e) {
      LOG.error("Unable to obtain SSH connection.", e);
      return false;
    }
  }

  public boolean logout() {
    try {
      client.disconnect();
      client = null;
      return true;
    } catch (IOException e) {
      LOG.error("Unable to end SSH connection.", e);
      return false;
    }
  }

  public ArrayList<RemoteFile> browseDir(RemoteFile dir) throws IOException {
    if (isBadConnection()) {
      throw new ConnectionException("SSH connection is bad. Try to log in again.");
    }

    SFTPClient sftp = client.newSFTPClient();
    try {
      List<RemoteResourceInfo> files = sftp.ls(dir.getFilePath(), mediaFileFilter);
      ArrayList<RemoteFile> fileList = new ArrayList<RemoteFile>(files.size());
      for (RemoteResourceInfo resource : files) {
        RemoteFile file = new RemoteFile();
        file.setDirectory(resource.isDirectory());
        file.setFilePath(resource.getPath());
        file.setParentPath(resource.getParent());
        fileList.add(file);
      }

      return fileList;
    } finally {
      sftp.close();
    }
  }

  public void play(String filePath) throws IOException {
    // TODO: open and cache shell
  }

  public void pause() throws IOException {
    // TODO: implement play/pause
  }

  public void volumeUp() throws IOException {
    // TODO: implement volume up
  }

  public void volumeDown() throws IOException {
    // TODO: implement volume down
  }

  public void stop() throws IOException {
    // TODO: quit control, close and clear shell
  }

  public void setProfile(Profile profile) {
    if (profile == null) {
      throw new IllegalArgumentException("You can't set null SSH profile to shell.");
    }
    this.profile = profile;
  }

  private boolean isBadConnection() {
    return client == null || !(client.isAuthenticated() && client.isConnected());
  }

  private static class FileFilter implements RemoteResourceFilter {
    private static final String ACCEPTED_TYPE = "video/";

    @Override
    public boolean accept(RemoteResourceInfo resource) {
      boolean accept = false;
      if (resource.isRegularFile()) {
        String path = resource.getPath();
        FileNameMap mimeTypeMap = URLConnection.getFileNameMap();
        String mimeType = mimeTypeMap.getContentTypeFor(path);
        accept = !TextUtils.isEmpty(mimeType) && mimeType.startsWith(ACCEPTED_TYPE);
      } else if (resource.isDirectory()) {
        accept = true;
      }

      return accept;
    }
  }
}
