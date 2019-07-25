package com.ilya.scheduleapp;

import android.content.Context;
import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;

import com.ilya.scheduleapp.helpers.StorageHelper;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class StorageTests {
    @Test
    public void checkSharedStorage_String() {
        Context appContext = InstrumentationRegistry.getTargetContext();

        StorageHelper.addToShared(appContext, "foo", "bar");

        String result = StorageHelper.findStringInShared(appContext, "foo");

        assertEquals("bar", result);
    }

    @Test
    public void checkSharedStorage_Int() {
        Context appContext = InstrumentationRegistry.getTargetContext();

        StorageHelper.addToShared(appContext, "foo", 228);

        int result = StorageHelper.findIntInShared(appContext, "foo");

        assertEquals(228, result);
    }

    @Test
    public void checkSharedStorage_StringSet() {
        Context appContext = InstrumentationRegistry.getTargetContext();
        String[] value = new String[] {"foo", "bar"};

        StorageHelper.addToShared(appContext, "foo", value);

        Set<String> result = StorageHelper.findStringSetInShared(appContext, "foo");

        assertEquals(new HashSet<>(Arrays.asList(value)), result);
    }
}
