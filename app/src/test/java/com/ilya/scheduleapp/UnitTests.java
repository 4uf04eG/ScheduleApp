package com.ilya.scheduleapp;

import com.ilya.scheduleapp.adapters.GroupsAdapter;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

public class UnitTests {
    @Test
    public void checkGroupsAdapterYearsDetection() {
        List<String> input = Arrays.asList("1 Year", "пр.", "8 day", "ФЫВЫ", "9", "Test");
        GroupsAdapter ga = new GroupsAdapter(input);

        for (int i = 0; i < input.size(); i++) {
            if (i % 2 == 0)
                assertEquals(1, ga.getItemViewType(i));
            else
                assertEquals(0, ga.getItemViewType(i));
        }
    }
}