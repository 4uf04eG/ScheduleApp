package com.ilya.scheduleapp.parsers;

import android.content.Context;
import android.os.AsyncTask;

import com.ilya.scheduleapp.helpers.StorageHelper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.URL;

public class GroupNameParser extends AsyncTask<Context, Integer, String> {
    private static final String SCHEDULE_LINK = "schedule_link";
    @Override
    protected String doInBackground(Context... contexts) {
        String link = StorageHelper.findStringInShared(contexts[0], SCHEDULE_LINK);

        try {
            Document doc = Jsoup.parse(new URL(link), 15000);
            return doc.select("p > font").get(1).text().split(" ")[0];
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        } catch (NullPointerException e) {
            e.printStackTrace();
            return "";
        }
    }
}
