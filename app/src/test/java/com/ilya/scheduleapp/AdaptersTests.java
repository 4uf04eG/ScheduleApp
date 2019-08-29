package com.ilya.scheduleapp;

import androidx.recyclerview.widget.RecyclerView;

import com.ilya.scheduleapp.adapters.GroupsAdapter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static junit.framework.TestCase.assertEquals;

@RunWith(PowerMockRunner.class)
@PrepareForTest(RecyclerView.class)
public class AdaptersTests {
    @Test
    public void groupsAdapter_TestNullValue() {
        GroupsAdapter ga = new GroupsAdapter();
        ga.refreshData(null);
    }

    @Test
    public void groupsAdapter_TestGetItemViewTYpe() {
        List<String> input = Arrays.asList("1 Year", "пр.", "8 day", "ФЫВЫ", "9", "Test");
        GroupsAdapter ga = new GroupsAdapter(input);

        for (int i = 0; i < input.size(); i++) {
            if (i % 2 == 0) {
                assertEquals(1, ga.getItemViewType(i));
            } else {
                assertEquals(0, ga.getItemViewType(i));
            }
        }
    }

    @Test
    public void groupsAdapter_TestGetItemCount() {
        int expectedCount = new Random().nextInt(30);
        List<String> input = new ArrayList<>();

        for (int i = 0; i < expectedCount; i++)
            input.add("");

        GroupsAdapter adapter = new GroupsAdapter(input);

        assertEquals(expectedCount, adapter.getItemCount());
    }
}