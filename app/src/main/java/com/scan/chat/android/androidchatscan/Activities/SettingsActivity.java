package com.scan.chat.android.androidchatscan.Activities;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

import com.scan.chat.android.androidchatscan.R;

import java.util.List;
import java.util.ArrayList;


/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends PreferenceActivity {


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    @Override
    protected boolean isValidFragment(String fragmentName)
    {
        ArrayList<Header> target = new ArrayList<>();
        loadHeadersFromResource(R.xml.pref_headers, target);
        for (Header h : target) {
            if (fragmentName.equals(h.fragment)) return true;
        }
        return false;
    }
    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class LoginInfoPreferenceFragment extends PreferenceFragment {

        private String username;
        private String password;
        EditTextPreference UsernameEditTextPref;
        EditTextPreference PasswdEditTextPref;
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            SharedPreferences sPrefs = getActivity().getSharedPreferences(MainActivity.PREFS_NAME, 0);
            username = sPrefs.getString("username", null);
            password = sPrefs.getString("password", null);
                // Bind the summaries of EditText/List/Dialog/Ringtone preferences
                // to their values. When their values change, their summaries are
                // updated to reflect the new value, per the Android Design
                // guidelines.
                //bindPreferenceSummaryToValue(findPreference("example_text"));
                //bindPreferenceSummaryToValue(findPreference("example_list"));

            //PreferenceManager.setDefaultValues(this, R.xml.pref_username, false);
            UsernameEditTextPref = (EditTextPreference)findPreference("name");
            PasswdEditTextPref = (EditTextPreference)findPreference("pwd");

            UsernameEditTextPref.setText(sPrefs.getString("name", "0"));
            PasswdEditTextPref.setText(sPrefs.getString("pwd", "0"));
            //String settings = getArguments().getString("settings");
           //if ("user".equals(settings))
           // {
            //UsernameEditTextPref.setDefaultValue(username);
           // PasswdEditTextPref.setDefaultValue(password);

            addPreferencesFromResource(R.xml.pref_username);

                // Bind the summaries of EditText/List/Dialog/Ringtone preferences
                // to their values. When their values change, their summaries are
                // updated to reflect the new value, per the Android Design
                // guidelines.
             //bindPreferenceSummaryToValue(findPreference("example_text"));
             //   bindPreferenceSummaryToValue(findPreference("example_list"));

            //}
            //else if ("pwd".equals(settings)) {
            //     addPreferencesFromResource(R.xml.pref_pwd);

               // Bind the summaries of EditText/List/Dialog/Ringtone preferences
               // to their values. When their values change, their summaries are
               // updated to reflect the new value, per the Android Design
               // guidelines.
               //bindPreferenceSummaryToValue(findPreference("notifications_new_message_ringtone"));
           //}
        }
    }

}
