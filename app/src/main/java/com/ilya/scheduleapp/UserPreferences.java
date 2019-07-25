package com.ilya.scheduleapp;

import android.app.Application;
import androidx.appcompat.app.AppCompatDelegate;

import com.ilya.scheduleapp.helpers.LocaleHelper;
import com.ilya.scheduleapp.helpers.StorageHelper;

public class UserPreferences extends Application {
    private static final String DARK_THEME_TYPE = "dark_theme";

    @Override
    public void onCreate() {
        LocaleHelper.loadLocale(this);

        String darkTheme = StorageHelper.findStringInShared(this, DARK_THEME_TYPE);

        if (darkTheme != null) {
            switch (darkTheme) {
                case "yes":
                    AppCompatDelegate.setDefaultNightMode
                            (AppCompatDelegate.MODE_NIGHT_YES);
                    break;
                case "no":
                    AppCompatDelegate.setDefaultNightMode
                            (AppCompatDelegate.MODE_NIGHT_NO);
                    break;
                case "auto":
                    AppCompatDelegate.setDefaultNightMode
                            (AppCompatDelegate.MODE_NIGHT_AUTO);
                    break;
            }
        }

        super.onCreate();
    }
}
