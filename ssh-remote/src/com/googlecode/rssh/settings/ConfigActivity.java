
package com.googlecode.rssh.settings;

import com.googlecode.rssh.R;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.text.TextUtils;

import java.io.IOException;

/**
 * @author Anton Novikov
 */
public class ConfigActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener {
  public static final String PROFILE = "profile_preference";

  public static final String HOST_NAME = "host_preference";

  public static final String PORT = "port_preference";

  public static final String USER_NAME = "user_name_preference";

  public static final String PASSWORD = "password_preference";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    addPreferencesFromResource(R.xml.settings);

    Preference profilePref = findPreference(PROFILE);
    profilePref.setOnPreferenceChangeListener(this);
  }

  @Override
  public boolean onPreferenceChange(Preference preference, Object newValue) {
    String prefKey = preference.getKey();
    if (PROFILE.equals(prefKey)) {
      String profileRes = (String) newValue;
      try {
        if (!TextUtils.isEmpty(profileRes)) {
          Profile profile = ProfileReader.readProfile(getApplicationContext(), profileRes);
        } else {
          // TODO: handle custom profile.
        }
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    return false;
  }
}
