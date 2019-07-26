package com.ilya.scheduleapp.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.NavUtils;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorChangedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.ilya.scheduleapp.R;
import com.ilya.scheduleapp.helpers.AppStyleHelper;
import com.ilya.scheduleapp.helpers.LocaleHelper;
import com.ilya.scheduleapp.helpers.StorageHelper;

public class SettingsActivity extends AppCompatActivity {
    private static final String DARK_THEME_TYPE = "dark_theme";
    private static final String SELECTED_LANGUAGE = "language";

    private SharedPreferences.OnSharedPreferenceChangeListener listener;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        NavUtils.navigateUpFromSameTask(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int theme = AppStyleHelper.getDefaultTheme(this);

        if (theme != Integer.MIN_VALUE)
            setTheme(theme);

        setContentView(R.layout.activity_settings);
        setTitle(R.string.action_settings);
/*
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsActivity.SettingsFragment())
                .commit();
*/
        AppStyleHelper.setDefaultBackground(this, getSupportActionBar());
    }

    public void onClearButtonClick(View view) {
        StorageHelper.clearShared(this);
    }

    private static void setLanguage(SettingsActivity activity) {
        LocaleHelper.loadLocale(activity);
        activity.restartActivity();
    }

    private static void setDarkTheme(SettingsActivity activity, String type) {
        switch (type) {
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

       // StorageHelper.addToShared(activity, DARK_THEME_TYPE, type);
   //     activity.restartActivity();
    }

    public void showColorPicker(final View view) {
        final Activity activity = this;

        AlertDialog dialog = ColorPickerDialogBuilder
                .with(this)
                .setTitle(R.string.settings_color_title)
                .lightnessSliderOnly()
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setOnColorChangedListener(new OnColorChangedListener() {
                    @Override
                    public void onColorChanged(int selectedColor) {
                        AppStyleHelper.setStyleDynamically
                                (activity, selectedColor, getSupportActionBar());
                    }
                })
                .setPositiveButton(R.string.confirmation, new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                        AppStyleHelper.saveColorScheme
                                (activity, selectedColor);
                    }
                })
                .setNegativeButton(R.string.cancellation, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AppStyleHelper.setDefaultStyle
                                (activity, getSupportActionBar());
                    }
                })
                .build();

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                AppStyleHelper.setDefaultStyle(activity, getSupportActionBar());
            }
        });

        dialog.show();
    }

/*
    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        listener = new SettingsListener(this);

        PreferenceManager.getDefaultSharedPreferences(this).
                registerOnSharedPreferenceChangeListener(listener);
    }

    @Override
    public void onStop() {
        super.onStop();
        PreferenceManager.getDefaultSharedPreferences(this).
                unregisterOnSharedPreferenceChangeListener(listener);
    }

    private class SettingsListener implements SharedPreferences.OnSharedPreferenceChangeListener {
        private SettingsActivity activity;

        private SettingsListener(SettingsActivity activity) { this.activity = activity; }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals(DARK_THEME_TYPE))
                setDarkTheme(activity, StorageHelper.findStringInShared(sharedPreferences, key));
            if (key.equals(SELECTED_LANGUAGE))
                setLanguage(activity);
        }
    }
*/
    private void restartActivity() {
        startActivity(getIntent());
        finish();
        overridePendingTransition(0, 0);
    }
}
