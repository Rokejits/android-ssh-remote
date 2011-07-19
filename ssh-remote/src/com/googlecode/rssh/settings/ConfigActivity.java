
package com.googlecode.rssh.settings;

import com.googlecode.rssh.R;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * @author Anton Novikov
 */
public class ConfigActivity extends PreferenceActivity {
  public static final String PROFILE = "profile_preference";

  public static final String HOST_NAME = "host_preference";

  public static final String PORT = "port_preference";

  public static final String USER_NAME = "user_name_preference";

  public static final String PASSWORD = "password_preference";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    addPreferencesFromResource(R.xml.settings);
  }
}
