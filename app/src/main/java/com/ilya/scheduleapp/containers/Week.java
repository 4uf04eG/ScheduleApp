package com.ilya.scheduleapp.containers;

import java.util.ArrayList;
import java.util.List;

public class Week {
    private List<List<String>> data;
    private int currentDay;

    public Week() {
        data = new ArrayList<>();
        currentDay = 0;
    }

    void add(String value) {
        while (currentDay >= size())
            data.add(new ArrayList<String>());

        data.get(currentDay).add(value);
    }

    void switchToNextDay() {
        while (currentDay >= size())
            data.add(new ArrayList<String>());

        currentDay++;
    }

    public List<String> get(int index) { return data.get(index); }

    public int size() { return data.size(); }
}
