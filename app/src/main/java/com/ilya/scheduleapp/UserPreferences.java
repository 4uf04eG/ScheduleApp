package com.ilya.scheduleapp;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

import com.ilya.scheduleapp.helpers.BackgroundHelper;
import com.ilya.scheduleapp.helpers.StorageHelper;

public class UserPreferences extends Application {
    private static final String DARK_THEME_TYPE = "dark_theme";

    @Override
    public void onCreate() {
        //LocaleHelper.loadLocale(this);

        boolean darkTheme = StorageHelper.findBooleanInShared(this, DARK_THEME_TYPE);

        if (darkTheme) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        if (StorageHelper.findScheduleInShared(this) != null) {
            BackgroundHelper.registerDefaultUpdateTask(this);
        } else {
            BackgroundHelper.removeUpdateTask(this);
        }

        super.onCreate();
    }
}
