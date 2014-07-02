package com.pushclient.app;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.util.Log;


public class SettingsActivity extends PreferenceActivity implements Preference.OnPreferenceClickListener {

    private static String COPY_DEV_ID_KEY = "device_id";
    static String INVOICE_SWITCH_KEY = "invoice_switch";
    static String PREDICT_SWITCH_KEY = "predict_switch";


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Add 'general' preferences.
        addPreferencesFromResource(R.xml.pref_general);

        // Add 'notifications' preferences, and a corresponding header.
        PreferenceCategory fakeHeader = new PreferenceCategory(this);
        fakeHeader.setTitle(R.string.pref_header_notifications);
        getPreferenceScreen().addPreference(fakeHeader);
        addPreferencesFromResource(R.xml.pref_notification);

        // Set Listener
        Preference devId = findPreference(COPY_DEV_ID_KEY);
        devId.setOnPreferenceClickListener(this);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (preference.hasKey() && COPY_DEV_ID_KEY.equals(preference.getKey())){
            copyDevIdToClipboard();
            return true;
        }
        return false;
    }

    private void copyDevIdToClipboard() {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        SharedPreferences prefs = getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
        String registrationId = "No device ID. Check your Internet connection.";
        if (prefs.contains(MainActivity.PROPERTY_REG_ID)) {
            registrationId = prefs.getString(MainActivity.PROPERTY_REG_ID, "");
            Log.i(MainActivity.TAG, "Registration ID from Preferences: " + registrationId);
        }
        ClipData data = ClipData.newPlainText(MainActivity.PROPERTY_REG_ID, registrationId);
        clipboard.setPrimaryClip(data);
    }
}

