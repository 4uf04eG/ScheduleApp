package com.ilya.scheduleapp.fragments;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.ilya.scheduleapp.R;
import com.ilya.scheduleapp.activities.MainActivity;
import com.ilya.scheduleapp.helpers.BackgroundHelper;
import com.ilya.scheduleapp.helpers.BackgroundHelper.UpdateFrequencies;
import com.ilya.scheduleapp.helpers.StorageHelper;


public class MoreFragment extends PreferenceFragmentCompat
    implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {
    private static final String DARK_THEME_TYPE = "dark_theme";
    private static final String SCHEDULE_LINK = "schedule_link";
    private static final String UPDATE_FREQUENCY = "schedule_update_frequency";

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
        findPreference("open_online").setOnPreferenceClickListener(this);
        findPreference("clear_shared").setOnPreferenceClickListener(this);
        findPreference("dark_theme").setOnPreferenceChangeListener(this);
        findPreference("group_name").setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        switch (preference.getKey()) {
            case "dark_theme":
                StorageHelper.addToShared(requireContext(), DARK_THEME_TYPE, newValue);
                if ((boolean) newValue) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }

                break;

            case "group_name":
                preference.getSummary();
                break;
        }

        return false;
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        switch (preference.getKey()) {
            case "open_online":
                openLinkInBrowser();
                return true;
            case "clear_shared":
                StorageHelper.clearShared(requireContext());
                return true;
        }
        return false;
    }

    private void openLinkInBrowser() {
        String link = StorageHelper.findStringInShared(requireContext(), SCHEDULE_LINK);

        if (link != null) {
            if (!link.startsWith("http://") && !link.startsWith("https://")) {
                link = "http://" + link;
            }

            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link)));
        }
    }


    private void showWeekChangeDialog() {
        final Context context = requireContext();
        final MainActivity activity = (MainActivity) requireActivity();
        String[] listItems = activity.getResources().getStringArray(R.array.update_frequency);
        int selectedPos = StorageHelper.findIntInShared(context, UPDATE_FREQUENCY);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.more_update_frequency_selector);
        builder.setSingleChoiceItems(listItems, selectedPos, (dialog, which) -> {
            switch (UpdateFrequencies.toEnum(which)) {
                case EVERY_DAY:
                    BackgroundHelper.replaceUpdateTask(context, 1);
                    StorageHelper.addToShared(context, UPDATE_FREQUENCY, which);
                    break;
                case EVERY_WEEK:
                    BackgroundHelper.replaceUpdateTask(context, 7);
                    StorageHelper.addToShared(context, UPDATE_FREQUENCY, which);
                    break;
                case EVERY_MONTH:
                    BackgroundHelper.replaceUpdateTask(context, 30);
                    StorageHelper.addToShared(context, UPDATE_FREQUENCY, which);
                    break;
                case NEVER:
                    BackgroundHelper.removeUpdateTask(context);
                    StorageHelper.addToShared(context, UPDATE_FREQUENCY, which);
                    break;
            }

            dialog.cancel();
        });


        builder.create().show();
    }
}
