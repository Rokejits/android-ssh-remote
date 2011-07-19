
package com.googlecode.rssh;

import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.rssh.settings.ConfigActivity;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

@EActivity(R.layout.main)
public class SshRemoteActivity extends Activity {

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
}
