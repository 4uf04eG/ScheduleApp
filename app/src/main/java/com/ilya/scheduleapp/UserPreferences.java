package com.ilya.scheduleapp;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

import com.ilya.scheduleapp.helpers.BackgroundHelper;
import com.ilya.scheduleapp.helpers.BackgroundHelper.UpdateFrequencies;
import com.ilya.scheduleapp.helpers.StorageHelper;

import static com.ilya.scheduleapp.helpers.BackgroundHelper.UpdateFrequencies.NEVER;

public class UserPreferences extends Application {
    private static final String DARK_THEME_TYPE = "dark_theme";
    private static final String UPDATE_FREQUENCY = "schedule_update_frequency";

    @Override
    public void onCreate() {
        setDarkTheme();
        setUpdateTask();
        super.onCreate();
    }

    private void setDarkTheme() {
        boolean darkTheme = StorageHelper.findBooleanInShared(this, DARK_THEME_TYPE);

        if (darkTheme) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    private void setUpdateTask() {
        int updateStatus = StorageHelper.findIntInShared(this, UPDATE_FREQUENCY);
        boolean isUpdateDisabled = UpdateFrequencies.toEnum(updateStatus) == NEVER;

        if (!BackgroundHelper.isTaskRegistered(this) && !isUpdateDisabled) {
            BackgroundHelper.registerUpdateTask(this, 7);
        }
    }
}
