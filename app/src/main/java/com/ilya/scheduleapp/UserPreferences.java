package com.ilya.scheduleapp;

import android.app.Application;
import android.os.Build;

import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;
import com.ilya.scheduleapp.helpers.BackgroundHelper;
import com.ilya.scheduleapp.helpers.BackgroundHelper.UpdateFrequencies;
import com.ilya.scheduleapp.helpers.StorageHelper;
import com.ilya.scheduleapp.utils.TLSSocketFactory;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

import static com.ilya.scheduleapp.helpers.BackgroundHelper.UpdateFrequencies.NEVER;

public class UserPreferences extends Application {
    private static final String DARK_THEME_TYPE = "dark_theme";
    private static final String UPDATE_FREQUENCY = "schedule_update_frequency";

    @Override
    public void onCreate() {
        setDarkTheme();
        setUpdateTask();
        updateAndroidSecurityProvider();
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

    private void updateAndroidSecurityProvider() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            try {
                final GoogleApiAvailability availability = GoogleApiAvailability.getInstance();
                final int status = availability.isGooglePlayServicesAvailable(this);

                SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
                sslContext.init(null, null, null);

                if (status == ConnectionResult.SUCCESS) {
                    ProviderInstaller.installIfNeeded(this);
                    sslContext.createSSLEngine();
                } else {
                    HttpsURLConnection.setDefaultSSLSocketFactory(
                            new TLSSocketFactory(sslContext.getSocketFactory()));
                }
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (KeyManagementException e) {
                e.printStackTrace();
            } catch (GooglePlayServicesRepairableException e) {
                e.printStackTrace();
            } catch (GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            }
        }
    }
}
