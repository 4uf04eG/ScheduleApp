package com.ilya.scheduleapp.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.ilya.scheduleapp.R;
import com.ilya.scheduleapp.adapters.GroupsAdapter;
import com.ilya.scheduleapp.containers.GroupsContainer;
import com.ilya.scheduleapp.helpers.AppStyleHelper;
import com.ilya.scheduleapp.helpers.StorageHelper;
import com.ilya.scheduleapp.listeners.GroupsAsyncTaskListener;
import com.ilya.scheduleapp.parsers.GroupsParser;

import java.util.concurrent.ExecutionException;

public class AllGroupsActivity extends AppCompatActivity implements GroupsAsyncTaskListener {
    private static final String GROUP_NAME = "group_name";
    private static final String SCHEDULE_LINK = "schedule_link";

    private SwipeRefreshLayout refreshLayout;
    private RecyclerView groupsView;
    private GroupsContainer foundGroups;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int theme = AppStyleHelper.getDefaultTheme(this);

        if (theme != Integer.MIN_VALUE)
            setTheme(theme);

        setContentView(R.layout.activity_all_groups);
        setTitle(R.string.group_selection);
        AppStyleHelper.setDefaultBackground(this, getSupportActionBar());

        groupsView = findViewById(R.id.groups);
        groupsView.setLayoutManager(new LinearLayoutManager(this));
        groupsView.setAdapter(new GroupsAdapter());

        refreshLayout = findViewById(R.id.swipe_refresh);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                tryLoadAllGroups();
            }
        });

        tryLoadAllGroups();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    public void showToast(int stringId, int duration) {
        Toast.makeText(this, getString(stringId), duration).show();
    }

    @Override
    public void showRetryMessage() {
        if (groupsView.getChildCount() == 0) {
            refreshLayout.setEnabled(false);
            findViewById(R.id.groups_retry_refresh).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void finishRefreshing() {
        refreshLayout.setEnabled(true);
        refreshLayout.setRefreshing(false);
        findViewById(R.id.groups_progress_bar).setVisibility(View.GONE);
        findViewById(R.id.groups_retry_refresh).setVisibility(View.GONE);
    }

    @Override
    public void addGroupsToView(GroupsContainer groups) {
        GroupsAdapter adapter = (GroupsAdapter) groupsView.getAdapter();

        if (adapter != null)
            adapter.refreshData(groups.toLinearList());
    }

    private void tryLoadAllGroups() {
        final GroupsParser sg = new GroupsParser(this);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    foundGroups = sg.execute().get();
                } catch (InterruptedException e) {
                    Log.d("HandledInterruptedE", e.getMessage());
                } catch (ExecutionException e) {
                    Log.d("HandledExecutionE", e.getMessage());
                }
            }
        }).start();
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setCancelable(true);
        builder.setTitle("Internal error");
        builder.setMessage("Please select another group");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    public void writeGroupToSharedStorage(View view) {
        String name = ((TextView) view).getText().toString();

        StorageHelper.addToShared(this, GROUP_NAME, name);
        StorageHelper.addToShared(this, SCHEDULE_LINK, foundGroups.findLink(name));

        if (StorageHelper.findStringInShared(this, SCHEDULE_LINK) == null)
            showAlertDialog();
        else
            finish();
    }

    public void onRefreshButtonClick(View view) {
        findViewById(R.id.groups_progress_bar).setVisibility(View.VISIBLE);
        findViewById(R.id.groups_retry_refresh).setVisibility(View.GONE);
        tryLoadAllGroups();
    }
}
