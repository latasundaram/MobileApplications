package com.example.Lata.weatherapp;

import android.content.SharedPreferences;
import android.preference.PreferenceActivity;
import android.os.Bundle;
import android.widget.Toast;

public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener{

    private SharedPreferences.Editor editor;
    private SharedPreferences sharedPreferences;
    private SharedPreferences sharedPref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        /*haredPref=getSharedPreferences("TempPreference",MODE_PRIVATE);
        SharedPreferences.OnSharedPreferenceChangeListener listener =
                new SharedPreferences.OnSharedPreferenceChangeListener() {
                    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                        sharedPreferences=SettingsActivity.this.getSharedPreferences(MainActivity.PREFS_NAME,MODE_PRIVATE);
                        editor=sharedPreferences.edit();
                        editor.putString(MainActivity.TEMP_UNIT, prefs.getString("TempPreference",""));
                        Log.d("demo",sharedPreferences.getString(MainActivity.TEMP_UNIT,""));
                    }
                };
        sharedPref.registerOnSharedPreferenceChangeListener(listener);*/
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences Preferences, String key) {
        sharedPreferences=this.getSharedPreferences(MainActivity.PREFS_NAME,MODE_PRIVATE);
        editor=sharedPreferences.edit();
        if(Preferences.getString(key,"").equals("1")) {
            editor.putString(MainActivity.TEMP_UNIT, "Celcius");
            editor.commit();
            Toast.makeText(this, "Temperature Unit has been changed to °C" +
                    " from °F", Toast.LENGTH_SHORT).show();
        }
        else if(Preferences.getString(key,"").equals("2")){
            editor.putString(MainActivity.TEMP_UNIT, "Fahrenheit");
            editor.commit();
            Toast.makeText(this, "Temperature Unit has been changed to °F" +
                    " from °C", Toast.LENGTH_SHORT).show();
        }

    }
}
