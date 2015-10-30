package com.scan.chat.android.androidchatscan.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;

import com.scan.chat.android.androidchatscan.R;

public class SettingsActivity extends PreferenceActivity {

    public static final String THEMES_PREFS = "ThemePrefs";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings);

        //get a handler to the theme preference and define onPreferenceChange() behavior
        ListPreference pref = (ListPreference)findPreference(THEMES_PREFS);
        pref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference,
                                              Object newValue) {

                //add or change theme value in user's preferences
                SharedPreferences spref = getSharedPreferences(MainActivity.PREFS_NAME, 0);
                SharedPreferences.Editor editor = spref.edit();
                editor.putInt("theme", Integer.parseInt(newValue.toString()));
                editor.commit();

                //get back to chat activity (we actually finish the current chat activity
                //to make sure we keep a single instance and start a new one)
                Intent intent = new Intent(SettingsActivity.this, ChatActivity.class);
                ChatActivity.mChatActivity.finish();
                startActivity(intent);
                finish();

                return true;
            }

        });

    }
}
