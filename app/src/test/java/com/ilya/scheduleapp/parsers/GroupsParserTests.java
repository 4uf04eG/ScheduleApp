package com.ilya.scheduleapp.parsers;

import android.content.res.Resources;

import com.ilya.scheduleapp.activities.AllGroupsActivity;
import com.ilya.scheduleapp.containers.GroupsContainer;
import com.ilya.scheduleapp.helpers.StorageHelper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ StorageHelper.class })
public class GroupsParserTests {
    @Mock
    AllGroupsActivity listener;
    @Mock
    Resources resources;

    @Test
    public void groupsParser_TestDoInBackground() {
        String[] links = new String[] {
                "http://www.ulstu.ru/schedule/students/part1/raspisan.htm",
                "http://www.ulstu.ru/schedule/students/part2/raspisan.htm",
                "http://www.ulstu.ru/schedule/students/part2/raspisan.htm"
        };

        mockStatic(StorageHelper.class);
        when(listener.getResources()).thenReturn(resources);
        when(resources.getStringArray(anyInt())).thenReturn(links);
        GroupsContainer container = new GroupsParser(listener).doInBackground();

        assertNotEquals(container.size(), 0);
    }

    @Test
    public void groupsParser_TestPostExecute_NotFound() {
        GroupsContainer container = new GroupsContainer();

        doNothing().when(listener).finishRefreshing();
        doAnswer(invocation -> {
            assertEquals("Not found", "Found");
            return null;
        }).when(listener).addGroupsToView(any(GroupsContainer.class));

        new GroupsParser(listener).onPostExecute(container);
    }

    @Test
    public void groupsParser_TestPostExecute_Found() {
        GroupsContainer container = new GroupsContainer();

        doNothing().when(listener).finishRefreshing();
        doAnswer(invocation -> {
            assertEquals("Found", "Not found");
            return null;
        }).when(listener).showErrorToast();
        container.add("Some", "Data");

        new GroupsParser(listener).onPostExecute(container);
    }
}
