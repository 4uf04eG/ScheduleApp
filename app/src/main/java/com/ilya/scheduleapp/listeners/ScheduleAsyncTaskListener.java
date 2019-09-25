package com.ilya.scheduleapp.listeners;

import android.content.Context;

import com.ilya.scheduleapp.containers.ScheduleContainer;

public interface ScheduleAsyncTaskListener {
    void addScheduleToView(ScheduleContainer scheduleContainer);

    void storeSchedule(Context context, ScheduleContainer scheduleContainer);

    void showErrorToast(Context context);

    void finishRefreshing();

    void reloadSchedule();
}
