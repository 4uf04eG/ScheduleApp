package com.ilya.scheduleapp.parsers;

import android.content.Context;
import android.util.Log;

import com.ilya.scheduleapp.activities.AllGroupsActivity;
import com.ilya.scheduleapp.containers.ScheduleContainer;
import com.ilya.scheduleapp.fragments.ScheduleFragment;
import com.ilya.scheduleapp.helpers.StorageHelper;
import com.ilya.scheduleapp.listeners.GroupsAsyncTaskListener;

import org.jsoup.Jsoup;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ StorageHelper.class, Log.class, Jsoup.class })
public class ScheduleParserTests {
    @Mock
    Context context;

    private List<String> groups;

    @Before
    public void setUp() {
        Set<String> links = new HashSet<>(Arrays.asList(
                "http://www.ulstu.ru/schedule/students/part1/raspisan.htm",
                "http://www.ulstu.ru/schedule/students/part2/raspisan.htm"));
        GroupsAsyncTaskListener listener = mock(AllGroupsActivity.class);

        mockStatic(StorageHelper.class);
        when(StorageHelper.findStringSetInShared(any(Context.class), anyString())).
                thenReturn(links);

        groups = new GroupsParser(listener).doInBackground().getAllLinks();
    }

    @Test
    public void scheduleParser_TestAllLinks() throws InterruptedException {
        mockStatic(Log.class);
        when(Log.e(anyString(), anyString())).thenAnswer(invocation -> {
            assertEquals("Parsed", invocation.getArguments()[1]);
            return null;
        });

        for (int pos = 0; pos < groups.size(); pos++) {
            when(StorageHelper.findStringInShared(context, "schedule_link")).
                    thenReturn(groups.get(pos));
            new ScheduleParser(context).doInBackground();
            Thread.sleep(10);
        }
    }

    @Test
    public void scheduleParser_TestPostExecute_Found() {
        ScheduleContainer input = new ScheduleContainer();
        ScheduleFragment listener = spy(ScheduleFragment.class);

        when(listener.getContext()).thenReturn(context);
        doNothing().when(listener).finishRefreshing();
        doAnswer(invocation -> {
            assertEquals("Not empty", "Empty");
            return null;
        }).when(listener).showErrorToast(context);

        input.add("Some data");
        new ScheduleParser(listener).onPostExecute(input);
    }

    @Test
    public void scheduleParser_TestPostExecute_NotFound() {
        ScheduleContainer input = new ScheduleContainer();
        ScheduleFragment listener = spy(ScheduleFragment.class);

        when(listener.getContext()).thenReturn(context);
        doNothing().when(listener).finishRefreshing();
        doNothing().when(listener).showErrorToast(context);
        doAnswer(invocation -> {
            assertEquals("Empty", "NotEmpty");
            return null;
        }).when(listener).addScheduleToView(any(ScheduleContainer.class));

        new ScheduleParser(listener).onPostExecute(input);
    }

    @Test
    public void scheduleParser_TestPostExecute_WithContext() throws NullPointerException {
        new ScheduleParser(context).onPostExecute(new ScheduleContainer());
    }

    @Test
    public void scheduleParser_DoInBackground_HandleException() throws IOException {
        mockStatic(Jsoup.class);
        String mockStr = "https://www.google.com/";

        when(StorageHelper.findStringInShared(any(Context.class), anyString())).
                thenReturn(mockStr);
        when(Jsoup.parse(any(URL.class), anyInt())).thenThrow(IOException.class);

        new ScheduleParser(context).doInBackground();
    }
}
