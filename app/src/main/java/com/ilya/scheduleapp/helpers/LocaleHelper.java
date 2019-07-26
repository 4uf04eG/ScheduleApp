package com.ilya.scheduleapp.helpers;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;

import java.util.Locale;

public class LocaleHelper {
    private static final String SELECTED_LANGUAGE = "language";

    public static void loadLocale(Context context) {
        updateResources(context, getLanguage(context));
    }

    public static void initializeLocale(Context context) {
        StorageHelper.addToShared(context, SELECTED_LANGUAGE, getDefaultLocale(context).toString());
    }


    public static void setLocale(Context context, String language) {
        StorageHelper.addToShared(context, SELECTED_LANGUAGE, language);

        updateResources(context, language);
    }

    private static String getLanguage(Context context) {
        String lang = StorageHelper.findStringInShared(context, SELECTED_LANGUAGE);

        return lang != null ? lang : getDefaultLocale(context).toString();
    }

    @SuppressWarnings("deprecation")
    private static Locale getDefaultLocale(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            return context.getResources().getConfiguration().getLocales().get(0);
        else
            return context.getResources().getConfiguration().locale;
    }

    private static void updateResources(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Resources resources = context.getResources();

        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;

        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
    }
}
