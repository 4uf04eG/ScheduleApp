package com.ilya.scheduleapp.listeners;

import com.ilya.scheduleapp.containers.GroupsContainer;

public interface GroupsAsyncTaskListener {
    void addGroupsToView(GroupsContainer result);

    void showToast(int stringId, int duration);

    void finishRefreshing();

    void showRetryMessage();
}
