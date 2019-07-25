package com.ilya.scheduleapp.parsers;

import android.content.Context;
import android.os.AsyncTask;

import com.ilya.scheduleapp.containers.ScheduleContainer;
import com.ilya.scheduleapp.helpers.StorageHelper;
import com.ilya.scheduleapp.listeners.ScheduleAsyncTaskListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;

public class ScheduleParser extends AsyncTask<Void, Integer, ScheduleContainer> {
    private static final String SCHEDULE_LINK = "schedule_link";

    private final ScheduleAsyncTaskListener listener;

    public ScheduleParser(ScheduleAsyncTaskListener listener) {
        this.listener = listener;
    }

    @Override
    protected ScheduleContainer doInBackground(Void... voids) {
        String link = StorageHelper.findStringInShared((Context) listener, SCHEDULE_LINK);
        ScheduleContainer schedule = new ScheduleContainer();
        Elements tables = null;

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

                        if(!text.isEmpty())
                            schedule.add(String.format(Locale.US,"%d) %s", periodsCount, text));
                    }
                }

                if(periodsCount > 5)
                    schedule.switchToNextDay();
            }

            schedule.switchToNextWeek();
        }

        return schedule;
    }

    @Override
    protected void onPostExecute(ScheduleContainer scheduleContainer) {
        super.onPostExecute(scheduleContainer);
        listener.addScheduleToView(scheduleContainer);
    }
}
