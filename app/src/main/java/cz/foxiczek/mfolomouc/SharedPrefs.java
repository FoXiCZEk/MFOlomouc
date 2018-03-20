package cz.foxiczek.mfolomouc;

/**
 * Created by liskbpet on 4. 9. 2017.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;



public class SharedPrefs {
    Context context;



    public SharedPrefs(Context context) {
        this.context = context;
    }

    public boolean readConfigBoolean(String name) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        boolean value = sharedPref.getBoolean(name, false);
        return value;
    }

    public String readConfigString(String name) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String value = sharedPref.getString(name, "");
        return value;
    }

    public int readConfigInt(String name) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        int value = sharedPref.getInt(name, 0);
        return value;
    }

    public float readConfigFloat(String name) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        float value = sharedPref.getFloat(name, 0f);
        return value;
    }

    public void setConfig(String name, boolean value) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(name, value);
        editor.commit();
    }

    public void setConfig(String name, String value) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(name, value);
        editor.commit();
    }

    public void setConfig(String name, int value) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(name, value);
        editor.commit();
    }

    public void setConfig(String name, float value) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putFloat(name, value);
        editor.commit();
    }
}

