package com.ilya.scheduleapp.parsers;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.ilya.scheduleapp.containers.ScheduleContainer;
import com.ilya.scheduleapp.helpers.StorageHelper;
import com.ilya.scheduleapp.listeners.ScheduleAsyncTaskListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.Locale;

public class ScheduleParser extends AsyncTask<Void, Integer, ScheduleContainer> {
    private static final String SCHEDULE_LINK = "schedule_link";

    private final ScheduleAsyncTaskListener listener;
    private final WeakReference<Context> context;

    public ScheduleParser(@NonNull ScheduleAsyncTaskListener listener) {
        this.listener = listener;
        context = new WeakReference<>(((Fragment) listener).requireContext());
    }

    public ScheduleParser(@NonNull Context context) {
        listener = null;
        this.context = new WeakReference<>(context);
    }

    /**
     * It gets schedule from specified link located in storage.
     * Link being converted to url and parsed.
     * If site couldn't be parsed or not reachable then it returns empty {@link ScheduleContainer}.
     * Otherwise it checks every table, row and column if the inner text is empty or starts with
     * "пр.", "лек." or "лаб.". That way it's possible to separate cells containing information like
     * call schedule and days of week from cells with schedule.
     * If it matches that regex then class count is increased and if it's not empty
     * string containing current class with count added to container.
     *
     * Switching days in container occurs only if class count is more than 5.
     * It's needed to ignore rows containing empty cells which are not part of a schedule
     *
     * @return contains schedule grouped by weeks and days
     */
    @Override
    protected ScheduleContainer doInBackground(Void... voids) {
        Elements tables = null;
        ScheduleContainer schedule = new ScheduleContainer();
        String link = StorageHelper.findStringInShared(context.get(), SCHEDULE_LINK);

        try {
            Document doc = Jsoup.parse(new URL(link), 15000);
            tables = doc.select("table");
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (tables == null) return schedule;

        for (Element week : tables) {
            for (Element day : week.select("tr")) {
                int periodsCount = 0;

                for (Element period : day.select("td")) {
                    String text = period.text();

                    if (text.toLowerCase().matches("^(пр.|лек.|лаб.|$).*")) {
                        periodsCount++;

                        if (!text.isEmpty()) {
                            schedule.add(String.format(Locale.US, "%d) %s", periodsCount, text));
                        }
                    }
                }

                if (periodsCount > 5) {
                    schedule.switchToNextDay();
                }
            }

            schedule.switchToNextWeek();
        }

        return schedule;
    }

    /**
     * @param scheduleContainer result of async operation
     */
    @Override
    protected void onPostExecute(ScheduleContainer scheduleContainer) {
        super.onPostExecute(scheduleContainer);
        if (listener == null) return;

        listener.finishRefreshing();

        if (scheduleContainer.size() != 0) {
            if (!StorageHelper.isScheduleChanged(context.get(), scheduleContainer)) {
                return;
            }

            listener.addScheduleToView(scheduleContainer);
            listener.storeSchedule(context.get(), scheduleContainer);
        } else {
            listener.showErrorToast(context.get());
        }
    }
}
