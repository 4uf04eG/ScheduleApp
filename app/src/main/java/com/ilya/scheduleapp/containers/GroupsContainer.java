package com.ilya.scheduleapp.containers;


import java.util.ArrayList;
import java.util.List;

public class GroupsContainer {
    private final List<List<Pair>> data;
    private int currentRow;

    public GroupsContainer() {
        data = new ArrayList<>();
        currentRow = 0;
    }

    public void add(String name, String value) {
        while (currentRow >= size())
            data.add(new ArrayList<Pair>());

        data.get(currentRow).add(new Pair(name, value));
    }

    public String findLink(String name) {
        for (List<Pair> row : data)
            for (Pair item : row)
                if (item.name.equals(name))
                    return item.link;

        return null;
    }

    public List<String> toLinearList() {
        if (size() == 0) return null;

        List<String> linearGroups = new ArrayList<>();

        for (int column = 0; column < data.get(0).size(); column++)
            for (int row = 0; row < size(); row++)
                if (column < data.get(row).size() &&
                        !Character.isDigit(data.get(row).get(column).name.charAt(0)) || row == 0)
                    linearGroups.add(data.get(row).get(column).name);

        return linearGroups;
    }

    public void switchToNextRow() { currentRow++; }

    public int size() { return data.size(); }
}
