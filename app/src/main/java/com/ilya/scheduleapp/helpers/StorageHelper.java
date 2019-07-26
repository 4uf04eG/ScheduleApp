package com.ilya.scheduleapp.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.ilya.scheduleapp.containers.ScheduleContainer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class StorageHelper {
    private static final String SCHEDULE = "schedule";

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
        else Log.e("Writing to sharedPrefs", "Used wrong data type");

        editor.apply();
    }

    public static void addScheduleToShared(Context context, ScheduleContainer value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(
                SCHEDULE, Context.MODE_PRIVATE).edit();

        try {
            editor.putString(SCHEDULE, new Gson().toJson(value));
        } catch (JsonParseException e) {
            e.printStackTrace();
        }

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

    public static String findStringInShared(SharedPreferences preferences, String name) {

        return preferences.getString(name, null);
    }

    public static Set<String> findStringSetInShared(Context context, String name) {
        SharedPreferences editor = PreferenceManager.getDefaultSharedPreferences(context);

        return editor.getStringSet(name, null);
    }

    public static ScheduleContainer findScheduleInShared(Context context) {
        SharedPreferences editor = context.getSharedPreferences(
                SCHEDULE, Context.MODE_PRIVATE);
        ScheduleContainer schedule = null;

        try {
            String json = editor.getString(SCHEDULE, null);
            schedule = new Gson().fromJson(json, ScheduleContainer.class);
        } catch (JsonParseException e) {
            e.printStackTrace();
        }

        return schedule;
    }

    public static void clearShared(Context context) {
        SharedPreferences.Editor editor = PreferenceManager.
                getDefaultSharedPreferences(context).edit();
        SharedPreferences.Editor editorSc = context.getSharedPreferences(
                SCHEDULE, Context.MODE_PRIVATE).edit();

        editor.clear();
        editor.apply();
        editorSc.clear();
        editorSc.apply();
    }
}
