
package com.googlecode.rssh.settings;

import android.content.Context;
import android.content.res.AssetManager;
import android.text.TextUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author Anton Novikov
 */
public final class ProfileReader {
  private static final String VOL_UP = "volume_up";

  private static final String VOL_DOWN = "volume_down";

  private static final String PAUSE = "pause";

  private static final String STOP = "stop";

  private static final String REW = "rewind";

  private static final String FWD = "forward";

  private static final String APP_START = "app_start";

  /**
   * Creates an instance of ProfileReader.
   */
  private ProfileReader() {}

  public static Profile readProfile(Context appContext, String profileRes) throws IOException {
    if (TextUtils.isEmpty(profileRes)) {
      throw new IllegalArgumentException("File with profile configuration must be specified.");
    }
    AssetManager assetMgr = appContext.getAssets();
    InputStream profileStream = assetMgr.open(profileRes);
    Properties config = new Properties();
    config.load(profileStream);
    Profile profile = new Profile();
    profile.setStartApp(config.getProperty(APP_START));
    profile.setPause(config.getProperty(PAUSE));
    profile.setStartApp(config.getProperty(STOP));
    profile.setRewind(config.getProperty(REW));
    profile.setForward(config.getProperty(FWD));
    profile.setVolumeDown(config.getProperty(VOL_DOWN));
    profile.setVolumeUp(config.getProperty(VOL_UP));

    return profile;
  }
}
