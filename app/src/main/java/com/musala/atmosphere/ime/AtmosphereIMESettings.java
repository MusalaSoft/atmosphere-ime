package com.musala.atmosphere.ime;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;

public class AtmosphereIMESettings extends Activity {

    public static class SettingsFragment extends PreferenceFragment {

        private CheckBoxPreference displayImePreference;

        private CheckBoxPreference displayAnimationPreference;

        private Preference hardwareKeyboardNotice;

        private Preference imeNotSetNotice;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            initPreferences();
        }

        private void initPreferences() {
            addPreferencesFromResource(R.xml.prefs);

            hardwareKeyboardNotice = findPreference(getString(R.string.hardware_keyboard_notice_key));
            imeNotSetNotice = findPreference(getString(R.string.ime_not_set_notice_key));
            displayImePreference = (CheckBoxPreference) findPreference(getString(R.string.display_ime_key));
            displayAnimationPreference = (CheckBoxPreference) findPreference(getString(R.string.display_animation_key));

            displayImePreference.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    Boolean shouldDisplayIme = (Boolean) newValue;

                    if (!shouldDisplayIme) {
                        displayAnimationPreference.setChecked(false);
                    }

                    return true;
                }
            });

            refreshPreferences();
        }

        public void refreshPreferences() {
            if (deviceHasHardwareKeyboard()) {
                displayImePreference.setChecked(false);
                displayImePreference.setEnabled(false);

                displayAnimationPreference.setChecked(false);
                displayAnimationPreference.setEnabled(false);

                hardwareKeyboardNotice.setSummary(R.string.hardware_keyboard_notice_summary);
            } else {
                displayImePreference.setEnabled(true);
                displayAnimationPreference.setEnabled(true);
                hardwareKeyboardNotice.setSummary(R.string.empty);
            }

            if (!isAtmosphereImeSet()) {
                imeNotSetNotice.setSummary(R.string.ime_not_set_notice_summary);
            } else {
                imeNotSetNotice.setSummary(R.string.empty);
            }
        }

        private boolean isAtmosphereImeSet() {
            String id = Settings.Secure.getString(getActivity().getContentResolver(),
                                                  Settings.Secure.DEFAULT_INPUT_METHOD);

            return id.contains(AtmosphereIME.class.getSimpleName());
        }

        private boolean deviceHasHardwareKeyboard() {
            Configuration config = getResources().getConfiguration();
            return config.keyboard != Configuration.KEYBOARD_NOKEYS;
        }

    }

    private SettingsFragment settingsFragment;

    private BroadcastReceiver imeChangedBroadcastReceiver;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        setContentView(R.layout.activity_settings);
        PreferenceManager.setDefaultValues(this, R.xml.prefs, false);

        settingsFragment = new SettingsFragment();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(android.R.id.content, settingsFragment);
        fragmentTransaction.commit();

        imeChangedBroadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Intent.ACTION_INPUT_METHOD_CHANGED)) {
                    settingsFragment.refreshPreferences();
                }
            }
        };

        registerReceiver(imeChangedBroadcastReceiver, new IntentFilter(Intent.ACTION_INPUT_METHOD_CHANGED));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_switch_ime:
                showImePickerDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(imeChangedBroadcastReceiver);
    }

    private void showImePickerDialog() {
        InputMethodManager imeManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imeManager != null) {
            imeManager.showInputMethodPicker();
        }
    }

}
