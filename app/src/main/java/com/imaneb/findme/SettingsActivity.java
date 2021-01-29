package com.imaneb.findme;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SeekBarPreference;

public class SettingsActivity extends AppCompatActivity {
    SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
    SharedPreferences.Editor editor = pref.edit();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }

    }

    public class SettingsFragment extends PreferenceFragmentCompat {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.root_preferences);

            SeekBarPreference seekBar = (SeekBarPreference) findPreference("Age");
            if (seekBar != null) {
                seekBar.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        if (newValue instanceof Integer) {
                            Integer newValueInt;
                            try {
                                newValueInt = (Integer) newValue;
                            } catch (NumberFormatException nfe) {
                                Toast.makeText(getActivity(),
                                        "SeekBarPreference is a Integer, but it caused a NumberFormatException",
                                        Toast.LENGTH_SHORT).show();
                                return false;
                            }
                            editor.putInt("min_age", newValueInt );


                            return true;
                        } else {
                            String objType = newValue.getClass().getName();
                            Toast.makeText(getActivity(),
                                    "SeekBarPreference is not a Integer, it is" +objType,
                                    Toast.LENGTH_SHORT).show();
                            return false;
                        }
                    }
                });
            }

            ListPreference listPreference = (ListPreference) findPreference("gender");
            if (listPreference != null){
                listPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        if (newValue instanceof String) {
                            String newValueS;
                            try {
                                newValueS = (String) newValue;
                            } catch (Exception e) {
                                return false;
                            }
                            editor.putString("gender_users", newValueS);
                            return true;
                        } else {
                            return false;
                        }
                    }
                });
            }

            editor.commit();
        }

    }
}