package com.ilya.scheduleapp.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class StorageHelper {
    public static void addToShared(Context context, String name, Object value) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();

        if (value instanceof Integer)
            editor.putInt(name, (Integer) value);
        else if (value instanceof Long)
            editor.putLong(name, (Long) value);
        else if (value instanceof Float)
            editor.putFloat(name, (Float) value);
        else if (value instanceof Boolean)
            editor.putBoolean(name, (Boolean) value);
        else if (value instanceof String)
            editor.putString(name, (String) value);
        else if (value instanceof String[])
            editor.putStringSet(name, new HashSet<>(Arrays.asList((String[]) value)));
        else Log.e("Writing to shared", "Used wrong data type");

        editor.apply();
    }

    public static int findIntInShared(Context context, String name) {
        SharedPreferences editor = PreferenceManager.getDefaultSharedPreferences(context);

        return editor.getInt(name, Integer.MIN_VALUE);
    }

    public static String findStringInShared(Context context, String name) {
        SharedPreferences editor = PreferenceManager.getDefaultSharedPreferences(context);

        return editor.getString(name, null);
    }

    public static Set<String> findStringSetInShared(Context context, String name) {
        SharedPreferences editor = PreferenceManager.getDefaultSharedPreferences(context);

        return editor.getStringSet(name, null);
    }

    public static void clearShared(Context context) {
        SharedPreferences.Editor editor = PreferenceManager.
                getDefaultSharedPreferences(context).edit();

        editor.clear();
        editor.apply();
    }
}
