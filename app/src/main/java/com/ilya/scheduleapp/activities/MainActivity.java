package com.ilya.scheduleapp.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.Preference;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ilya.scheduleapp.R;
import com.ilya.scheduleapp.fragments.AlarmsFragment;
import com.ilya.scheduleapp.fragments.CallScheduleFragment;
import com.ilya.scheduleapp.fragments.MoreFragment;
import com.ilya.scheduleapp.fragments.ScheduleFragment;
import com.ilya.scheduleapp.helpers.AppStyleHelper;
import com.ilya.scheduleapp.helpers.BackgroundHelper.UpdateFrequencies;
import com.ilya.scheduleapp.helpers.LocaleHelper;
import com.ilya.scheduleapp.helpers.StorageHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener {
    private static final String GROUP_LINKS = "group_links";
    private static final String SCHEDULE_LINK = "schedule_link";
    private static final String NUM_OF_WEEK = "current_week";
    private static final String UPDATE_FREQUENCY = "schedule_update_frequency";
    private static final String ARG_SAVED_FRAGMENT = "saved_fragment";
    private static final String ARG_VISIBILITY_STATUSES = "visibility_statuses";

    private static long bottomNavLastClickTime;
    private static boolean isGroupSelectorOpened;

    private Toolbar toolbar;
    private BottomNavigationView bottomNavigation;

    //TODO: Fix bug when after changing device locale app locale doesn't change

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(R.string.app_name);

        handleUrlIntent();

        toolbar = findViewById(R.id.toolbar);
        AppStyleHelper.restoreMainStyle(this, toolbar);
        setSupportActionBar(toolbar);

        bottomNavigation = findViewById(R.id.bottom_nav_view);
        bottomNavigation.setOnNavigationItemSelectedListener(this);

        if (savedInstanceState != null) {
            restoreFragments(savedInstanceState);
        } else if (StorageHelper.findStringInShared(this, SCHEDULE_LINK) != null) {
            loadFragment(new ScheduleFragment());
        } else {
            setInitialParameters();
            startActivityForResult(new Intent(this, AllGroupsActivity.class), 0);
            isGroupSelectorOpened = true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0) {
            isGroupSelectorOpened = false;
            loadFragment(new ScheduleFragment());
        }

        if (requestCode == 1) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction transaction = fm.beginTransaction();
            Fragment fragment = fm.findFragmentByTag("schedule");
            MoreFragment settings = (MoreFragment) fm.findFragmentByTag("more");
            Preference preference = settings.findPreference("group_name");

            settings.findPreference("group_name").setSummary(preference.getSummary());

            if (fragment != null) {
                transaction.remove(fragment).commit();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        List<Fragment> addedFragments = getSupportFragmentManager().getFragments();
        boolean[] visibilityStatuses = new boolean[addedFragments.size()];

        for (int i = 0; i < addedFragments.size(); i++) {
            getSupportFragmentManager().putFragment(outState, ARG_SAVED_FRAGMENT + i,
                    addedFragments.get(i));
            visibilityStatuses[i] = addedFragments.get(i).isVisible();
        }

        outState.putBooleanArray(ARG_VISIBILITY_STATUSES, visibilityStatuses);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        if (!isGroupSelectorOpened) {
            setFragmentBySelectedItemId(bottomNavigation.getSelectedItemId());
        }
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("schedule");

        if (fragment != null && fragment.isVisible()) {
            super.onBackPressed();
        } else {
            loadFragment(new ScheduleFragment());
            bottomNavigation.setSelectedItemId(R.id.navigation_schedule);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        long curTime = SystemClock.elapsedRealtime();

        if (curTime - bottomNavLastClickTime < 100) return false;

        bottomNavLastClickTime = curTime;

        return setFragmentBySelectedItemId(menuItem.getItemId());
    }

    @SuppressLint("SimpleDateFormat")
    public void changeToolbarSubtitle(boolean visible) {
        if (!visible) {
            toolbar.setSubtitle("");
            return;
        }

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("E MMM d");
        int weekNum = StorageHelper.findIntInShared(this, NUM_OF_WEEK) + 1;

        String subtitle = getResources().getString(R.string.schedule_toolbar_subtitle,
                format.format(calendar.getTime()), weekNum);
        toolbar.setSubtitle(subtitle);
    }

    private void handleUrlIntent() {
        Uri appLinkData = getIntent().getData();
        String regex = "http://www.ulstu.ru/schedule/students/part\\d+/\\d+.htm";

        //TODO: Later needs to create and add parser to get group's name
        if (appLinkData != null && appLinkData.toString().matches(regex)) {
            StorageHelper.addToShared(this, SCHEDULE_LINK, appLinkData.toString());
            StorageHelper.clearSchedule(this);
        }
    }

    private void setInitialParameters() {
        String[] urls = getResources().getStringArray(R.array.default_group_links);
        int defaultUpdateFrequency = UpdateFrequencies.EVERY_MONTH.ordinal();

        StorageHelper.addToShared(this, GROUP_LINKS, urls);
        StorageHelper.addToShared(this, NUM_OF_WEEK, 1);
        StorageHelper.addToShared(this, UPDATE_FREQUENCY, defaultUpdateFrequency);

        AppStyleHelper.initializeStyle(this);
        LocaleHelper.initializeLocale(this);
    }

    private boolean setFragmentBySelectedItemId(int id) {
        switch (id) {
            case R.id.navigation_schedule:
                loadFragment(new ScheduleFragment());
                return true;
            case R.id.navigation_call_schedule:
                loadFragment(new CallScheduleFragment());
                return true;
            case R.id.navigation_more:
                loadFragment(new MoreFragment());
                return true;
            default:
                return false;
        }
    }

    private void loadFragment(Fragment fragment) {
        if (fragment instanceof ScheduleFragment) {
            addFragment(fragment, "schedule");
            AppStyleHelper.restoreTabLayoutStyle(this);
        } else if (fragment instanceof MoreFragment) {
            addFragment(fragment, "more");
        } else if (fragment instanceof AlarmsFragment) {
            addFragment(fragment, "alarms");
        } else {
            addFragment(fragment, "call_schedule");
        }

        changeToolbarSubtitle(fragment instanceof ScheduleFragment);
    }

    private void addFragment(Fragment fragment, String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment cachedFragment = fragmentManager.findFragmentByTag(tag);
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        for (Fragment addedFragment : fragmentManager.getFragments()) {
            transaction.hide(addedFragment);
        }

        if (cachedFragment != null) {
            transaction.show(cachedFragment).commit();
        } else {
            transaction.add(R.id.main_container, fragment, tag).commit();
        }
    }

    private void restoreFragments(Bundle savedState) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        boolean[] visibilities = savedState.getBooleanArray(ARG_VISIBILITY_STATUSES);
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment;
        int i = 0;

        while ((fragment = fm.getFragment(savedState, ARG_SAVED_FRAGMENT + i)) != null) {
            Fragment cachedFragment = fm.findFragmentByTag(fragment.getTag());

            if (cachedFragment == null) transaction.add(R.id.main_container, fragment);

            if (visibilities != null && !visibilities[i]) {
                transaction.hide(fragment);
            } else {
                changeToolbarSubtitle(fragment instanceof ScheduleFragment);
            }

            i++;
        }
    }



    // How to restart activity the right way
    // private void restartActivity() {
    //        startActivity(getIntent());
    //        finish();
    //        overridePendingTransition(0, 0);
    //    }
}
