package com.ilya.scheduleapp.activities;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.ilya.scheduleapp.R;
import com.ilya.scheduleapp.adapters.ScheduleAdapter;
import com.ilya.scheduleapp.containers.ScheduleContainer;
import com.ilya.scheduleapp.helpers.AppStyleHelper;
import com.ilya.scheduleapp.helpers.StorageHelper;
import com.ilya.scheduleapp.listeners.ScheduleAsyncTaskListener;
import com.ilya.scheduleapp.parsers.ScheduleParser;

import java.util.Calendar;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ScheduleAsyncTaskListener {
    private static final String GROUP_LINKS = "group_links";
    private static final String DARK_THEME_TYPE = "dark_theme";
    private static final String SCHEDULE_LINK = "schedule_link";
    private static final String NUM_OF_WEEK = "current_week";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(R.string.app_name);

        RecyclerView view = findViewById(R.id.schedule_view);
        view.setLayoutManager(new LinearLayoutManager(this));
        view.setAdapter(new ScheduleAdapter());

        Toolbar toolbar = findViewById(R.id.toolbar);
        AppStyleHelper.restoreMainStyle(this, toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        handleUrlIntent();

        if (StorageHelper.findStringInShared(this, SCHEDULE_LINK) == null) {
            String[] urls = getResources().getStringArray(R.array.default_group_links);

            StorageHelper.addToShared(this, GROUP_LINKS, urls);
            StorageHelper.addToShared(this, DARK_THEME_TYPE, "no");
            StorageHelper.addToShared(this, NUM_OF_WEEK, 1);
            startActivityForResult(new Intent(this, AllGroupsActivity.class), 0);
        }

        tryLoadSchedule();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0)
            tryLoadSchedule();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_call_schedule)
            startActivity(new Intent(this, CallScheduleActivity.class));
        else if (id == R.id.nav_site_link)
            openLinkInBrowser();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START, false);

        return true;
    }

    @Override
    public void addScheduleToView(ScheduleContainer schedule) {
        RecyclerView view = findViewById(R.id.schedule_view);
        ScheduleAdapter adapter = (ScheduleAdapter) view.getAdapter();
        int numOfWeek = StorageHelper.findIntInShared(this, NUM_OF_WEEK);

        if(schedule.size() != 0 && adapter != null && numOfWeek != Integer.MIN_VALUE)
            adapter.refreshData(schedule.get(numOfWeek));

        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        Log.d("Date" , c.get(Calendar.DAY_OF_MONTH) + "");
    }

    public void onScheduleItemClicked(View view) { }

    private void openLinkInBrowser() {
        String link = StorageHelper.findStringInShared(this, SCHEDULE_LINK);

        if (link != null) {
            if (!link.startsWith("http://") && !link.startsWith("https://"))
                link = "http://" + link;

            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link)));
        }
    }

    private void tryLoadSchedule() {
        final ScheduleParser sp = new ScheduleParser(this);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    sp.execute().get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void handleUrlIntent() {
        Uri appLinkData = getIntent().getData();
        String regex = "http://www.ulstu.ru/schedule/students/part\\d+/\\d+.htm";

        if (appLinkData != null && appLinkData.toString().matches(regex))
            StorageHelper.addToShared(this, SCHEDULE_LINK, appLinkData.toString());
    }
}
