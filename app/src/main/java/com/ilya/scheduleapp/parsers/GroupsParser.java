package com.ilya.scheduleapp.parsers;


import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.ilya.scheduleapp.R;
import com.ilya.scheduleapp.containers.GroupsContainer;
import com.ilya.scheduleapp.helpers.StorageHelper;
import com.ilya.scheduleapp.listeners.GroupsAsyncTaskListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.Set;

public class GroupsParser extends AsyncTask<Context, Integer, GroupsContainer> {
    private static final String GROUP_LINKS = "group_links";

    private final GroupsAsyncTaskListener listener;

    public GroupsParser(GroupsAsyncTaskListener listener) {
        this.listener = listener;
    }

    @Override
    protected GroupsContainer doInBackground(Context... contexts) {
        GroupsContainer groups = new GroupsContainer();
        Set<String> urls = StorageHelper.findStringSetInShared((Context) listener, GROUP_LINKS);

        if (urls == null) return groups;

        try {
            for (String url : urls) {
                Document doc = Jsoup.parse(new URL(url), 15000);
                Elements rows = doc.select("tr");

                for (Element row : rows) {
                    Elements columns = row.select("td");

                    if (!columns.get(0).hasText()) continue;

                    for (Element column : columns) {
                        if (!column.hasText()) continue;

                        addGroup(groups, url, column);
                    }

                    groups.switchToNextRow();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return groups;
    }

    @Override
    protected void onPostExecute(GroupsContainer groupsContainer) {
        super.onPostExecute(groupsContainer);
        listener.finishRefreshing();

        if (groupsContainer.size() == 0) {
            listener.showToast(R.string.connection_error, Toast.LENGTH_SHORT);
            listener.showRetryMessage();
        } else
            listener.addGroupsToView(groupsContainer);
    }

    private void addGroup(GroupsContainer groups, String url, Element column) {
        String link = column.select("a").attr("href");

        if (!link.matches("^(https?://|www.)"))
            link = url.substring(0, url.lastIndexOf('/') + 1) + link;

        groups.add(column.text(), link);
    }
}


