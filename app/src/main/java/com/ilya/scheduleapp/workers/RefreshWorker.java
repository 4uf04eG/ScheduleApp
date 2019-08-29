package com.ilya.scheduleapp.workers;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.ilya.scheduleapp.containers.ScheduleContainer;
import com.ilya.scheduleapp.helpers.NotificationHelper;
import com.ilya.scheduleapp.helpers.StorageHelper;
import com.ilya.scheduleapp.parsers.ScheduleParser;

import java.util.concurrent.ExecutionException;

public class RefreshWorker extends Worker {
    private final Context context;

    public RefreshWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        ScheduleContainer result = new ScheduleContainer();
        try {
            result = new ScheduleParser(context).execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (StorageHelper.isScheduleChanged(context, result)) {
            StorageHelper.addScheduleToShared(context, result);
            NotificationHelper.showNotification(context);
        }

        return Result.success();
    }
}
