package com.ilya.scheduleapp.listeners;

import com.ilya.scheduleapp.containers.ScheduleContainer;

public interface ScheduleAsyncTaskListener{
    void addScheduleToView(ScheduleContainer scheduleContainer);

    void storeSchedule(ScheduleContainer scheduleContainer);
}
