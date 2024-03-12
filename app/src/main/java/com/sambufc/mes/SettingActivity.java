package com.sambufc.mes;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;

public class SettingActivity extends PreferenceActivity {

    SharedPreferences preferences;
    CheckBoxPreference preAutoId, preAutoPwd, preAutoLogin, preUseCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.addPreferencesFromResource(R.layout.activity_setting);
        preferences = getSharedPreferences("sambumobilemes", Context.MODE_PRIVATE);

        preAutoId = (CheckBoxPreference)findPreference("auto_id");
        preAutoPwd = (CheckBoxPreference)findPreference("auto_pwd");
        preAutoLogin = (CheckBoxPreference)findPreference("auto_login");
        preUseCamera = (CheckBoxPreference)findPreference("use_camera");

        preAutoId.setChecked(preferences.getBoolean("autoidCheck", false));
        preAutoPwd.setChecked(preferences.getBoolean("autopwdCheck", false));
        preAutoLogin.setChecked(preferences.getBoolean("autologinCheck", false));
        preUseCamera.setChecked(preferences.getBoolean("useCamera", false));

        preAutoId.setEnabled(!preAutoPwd.isChecked());
        preAutoPwd.setEnabled(preAutoId.isChecked() && !preAutoLogin.isChecked());
        preAutoLogin.setEnabled(preAutoPwd.isChecked());

        preAutoId.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                preAutoPwd.setEnabled(preAutoId.isChecked());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("autoidCheck", preAutoId.isChecked());
                editor.commit();
                return false;
            }
        });

        preAutoPwd.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                preAutoLogin.setEnabled(preAutoPwd.isChecked());
                preAutoId.setEnabled(!preAutoPwd.isChecked());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("autopwdCheck", preAutoPwd.isChecked());
                editor.commit();
                return false;
            }
        });

        preAutoLogin.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                preAutoPwd.setEnabled(!preAutoLogin.isChecked());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("autologinCheck", preAutoLogin.isChecked());
                editor.commit();
                return false;
            }
        });

        preUseCamera.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("useCamera", preUseCamera.isChecked());
                editor.commit();
                return false;
            }
        });
    }
}
